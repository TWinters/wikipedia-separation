package be.kuleuven.alsn;

import be.kuleuven.alsn.arguments.LinksFinderArguments;
import be.kuleuven.alsn.data.WikipediaPageCard;
import be.kuleuven.alsn.data.WikipediaPath;
import com.beust.jcommander.JCommander;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.neo4j.driver.v1.Values.parameters;

public class WikipediaLinksFinder implements AutoCloseable {

    private final Driver driver;

    public WikipediaLinksFinder(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    private static final String shortestPathQuery = "MATCH (begin:Page { title: $from }),(end:Page { title: $to }), p = shortestPath((begin)-[:REFERENCES_TO*]->(end)) RETURN p";

    public Collection<WikipediaPath> findShortestPath(final String from, final String to) {
        return extractPathsFromStatementResult(
                driver
                        .session()
                        .writeTransaction(tx ->
                                tx.run(shortestPathQuery, parameters("from", from, "to", to))));

    }

    // TODO: Optimaliseerbaar door gebruik van volgende query: 'MATCH (s) WHERE ID(s) in [19, 3309035] RETURN ID(s),s.title' voor meerdere nodes
    private static final String nodeNameQuery = "MATCH (s) WHERE ID(s) = $id RETURN s.title";


    private Collection<WikipediaPath> extractPathsFromStatementResult(StatementResult result) {
        // Using a set to filter out duplicate paths (due to duplicate IDs for pages)
        HashSet<WikipediaPath> paths = new HashSet<>();

        // For all found shortest paths
        for (Record rec : result.list()) {
            rec.asMap().forEach((key, value) ->
                    paths.add(convertPath((InternalPath) value)));
        }
        return paths;
    }

    private WikipediaPath convertPath(InternalPath path) {
        return new WikipediaPath(
                StreamSupport.stream(path.nodes()
                        .spliterator(), false)
                        .map(Entity::id)
                        .map(this::toWikipediaPageCard)
                        .collect(Collectors.toList()));
    }

    private WikipediaPageCard toWikipediaPageCard(final long nodeId) {
        return new WikipediaPageCard(nodeId, driver.session()
                .writeTransaction(tx ->
                        tx.run(nodeNameQuery, parameters("id", nodeId)))
                .single().get(0).asString());
    }

    public static void main(String... args) throws Exception {
        LinksFinderArguments arguments = new LinksFinderArguments();

        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);


        WikipediaLinksFinder finder = new WikipediaLinksFinder(arguments.getDatabaseUrl(), arguments.getLogin(), arguments.getPassword());
        System.out.println(finder.findShortestPath(arguments.getFrom(), arguments.getTo())
                .stream().map(WikipediaPath::toString)
                .collect(Collectors.joining("\n")));
    }
}
