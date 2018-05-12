package be.kuleuven.alsn.analysers;

import be.kuleuven.alsn.arguments.LinksFinderArguments;
import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPath;
import com.beust.jcommander.JCommander;
import com.google.common.collect.ImmutableMap;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.neo4j.driver.v1.Values.parameters;
import static org.neo4j.driver.v1.Values.value;

public class WikiPathFinder implements AutoCloseable {

    private static final String shortestPathQuery = "MATCH (begin:Page { title: $from }),(end:Page { title: $to }), p = shortestPath((begin)-[:REFERENCES_TO*]->(end)) RETURN p";
    // TODO: Optimaliseerbaar door gebruik van volgende query: 'MATCH (s) WHERE ID(s) in [19, 3309035] RETURN ID(s),s.title' voor meerdere nodes
    private static final String nodeNameQuery = "MATCH (s) WHERE ID(s) = $id RETURN s.title";
    private final Driver driver;

    private WikiPathFinder(Driver driver) {
        this.driver = driver;
    }

    public WikiPathFinder(Neo4jConnectionDetails neo4jArguments) {
        this(neo4jArguments.createConnection());
    }

    public static void main(String... args) throws Exception {
        Neo4jConnectionDetails neo4jArguments = new Neo4jConnectionDetails();
        LinksFinderArguments linkArguments = new LinksFinderArguments();

        JCommander.newBuilder()
                .addObject(neo4jArguments)
                .addObject(linkArguments)
                .build()
                .parse(args);


        WikiPathFinder finder = new WikiPathFinder(neo4jArguments);
        System.out.println(
                finder.findShortestPath(linkArguments.getFrom(), linkArguments.getTo(), linkArguments.getBlockedCommunities())
                        .stream().map(WikiPath::toString)
                        .collect(Collectors.joining("\n")));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public Collection<WikiPath> findShortestPath(
            final String from, final String to, Collection<WikiCommunityToken> blockedCommunities) {
        if (blockedCommunities.isEmpty()) {
            return findShortestPathSimple(from, to);
        } else {
            return findShortestPathExcludingClusters(from, to, blockedCommunities);
        }
    }

    private Collection<WikiPath> runPathFindingQuery(String query, Value parameters) {
        return extractPathsFromStatementResult(
                driver.session()
                        .writeTransaction(tx ->
                                tx.run(query, parameters)));

    }

    /**
     * Finds shortest path through the graph without blocking any cluster
     */
    private Collection<WikiPath> findShortestPathSimple(final String from, final String to) {
        return runPathFindingQuery(shortestPathQuery, parameters("from", from, "to", to));

    }

    /**
     * Finds shortest path though the graph but blocks nodes belonging to given communities
     */
    private Collection<WikiPath> findShortestPathExcludingClusters(
            String from, final String to, Collection<WikiCommunityToken> blockedCommunities) {
        List<WikiCommunityToken> tokens = new ArrayList<>(blockedCommunities);
        String query = createShortestPathQueryForCommunities(tokens);
        Value parameters = createParametersForCommunities(from, to, tokens);
        return runPathFindingQuery(query, parameters);


    }

    private String createShortestPathQueryForCommunities(List<WikiCommunityToken> blockedCommunities) {
        String query = "MATCH (begin:Page{title: $from})," +
                " (end:Page{title: $to})," +
                " p = shortestPath((begin)-[:REFERENCES_TO*]->(end))," +
                IntStream.range(0, blockedCommunities.size())
                        .mapToObj(i -> "(com" + i + ":Community{id:" + blockedCommunities.get(i).getId() + "})")
                        .collect(Collectors.joining(",")) + " " +
                " WHERE NONE (n IN FILTER (n IN nodes (p) WHERE NOT(n = begin OR n = end)) " +
                " WHERE (" +
                IntStream.range(0, blockedCommunities.size())
                        .mapToObj(i -> "EXISTS ((n) -[:PART_OF_COM]->(com" + i + "))")
                        .collect(Collectors.joining(" OR "))
                +
                ")) RETURN p ";
        return query;
    }

    private Value createParametersForCommunities(String from, String to,
                                                 List<WikiCommunityToken> blockedCommunities) {
        ImmutableMap.Builder<String, Object> b = ImmutableMap.builder();

        b.put("from", from);
        b.put("to", to);
        IntStream.range(0, blockedCommunities.size())
                .forEach(
                        i -> b.put("com" + i, blockedCommunities.get(i).getId())
                );
        return value(b.build());

    }

    private Collection<WikiPath> extractPathsFromStatementResult(StatementResult result) {
        // Using a set to filter out duplicate paths (due to duplicate IDs for pages)
        HashSet<WikiPath> paths = new HashSet<>();

        // For all found shortest paths
        for (Record rec : result.list()) {
            rec.asMap().forEach((key, value) ->
                    paths.add(convertPath((InternalPath) value)));
        }
        return paths;
    }

    private WikiPath convertPath(InternalPath path) {
        return new WikiPath(
                StreamSupport.stream(path.nodes()
                        .spliterator(), false)
                        .map(Entity::id)
                        .map(this::toWikipediaPageCard)
                        .collect(Collectors.toList()));
    }

    private WikiPageCard toWikipediaPageCard(final long nodeId) {
        return new WikiPageCard(nodeId, driver.session()
                .writeTransaction(tx ->
                        tx.run(nodeNameQuery, parameters("id", nodeId)))
                .single().get(0).asString());
    }
}
