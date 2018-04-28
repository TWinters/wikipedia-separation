package be.kuleuven.alsn.analysers;

import be.kuleuven.alsn.arguments.LinksFinderArguments;
import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPath;
import com.beust.jcommander.JCommander;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.neo4j.driver.v1.Values.parameters;

public class WikiPathFinder implements AutoCloseable {

    private final Driver driver;

    public WikiPathFinder(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public WikiPathFinder(Neo4jConnectionDetails neo4jArguments) {
        this(neo4jArguments.getDatabaseUrl(),
                neo4jArguments.getLogin(),
                neo4jArguments.getPassword());
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    private static final String shortestPathQuery = "MATCH (begin:Page { title: $from }),(end:Page { title: $to }), p = shortestPath((begin)-[:REFERENCES_TO*]->(end)) RETURN p";

    public Collection<WikiPath> findShortestPath(final String from, final String to) {
        return extractPathsFromStatementResult(
                driver.session()
                        .writeTransaction(tx ->
                                tx.run(shortestPathQuery, parameters("from", from, "to", to))));

    }

    // TODO: Optimaliseerbaar door gebruik van volgende query: 'MATCH (s) WHERE ID(s) in [19, 3309035] RETURN ID(s),s.title' voor meerdere nodes
    private static final String nodeNameQuery = "MATCH (s) WHERE ID(s) = $id RETURN s.title";


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

    public static void main(String... args) throws Exception {
        Neo4jConnectionDetails neo4jArguments = new Neo4jConnectionDetails();
        LinksFinderArguments linkArguments = new LinksFinderArguments();

        JCommander.newBuilder()
                .addObject(neo4jArguments)
                .addObject(linkArguments)
                .build()
                .parse(args);


        WikiPathFinder finder = new WikiPathFinder(neo4jArguments.getDatabaseUrl(), neo4jArguments.getLogin(), neo4jArguments.getPassword());
        System.out.println(finder.findShortestPath(linkArguments.getFrom(), linkArguments.getTo())
                .stream().map(WikiPath::toString)
                .collect(Collectors.joining("\n")));
    }
}
