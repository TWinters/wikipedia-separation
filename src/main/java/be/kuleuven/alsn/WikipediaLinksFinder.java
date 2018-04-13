package be.kuleuven.alsn;

import be.kuleuven.alsn.data.LinksFinderArguments;
import com.beust.jcommander.JCommander;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.driver.v1.*;

import java.util.HashSet;
import java.util.List;
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

    public void findShortestPath(final String from, final String to) {
        try (Session session = driver.session()) {
            HashSet<List<String>> shortestPaths =
                    session.writeTransaction(tx -> {
                        StatementResult statementResult = tx.run(shortestPathQuery,
                                parameters("from", from, "to", to));
                        return extractPathsFromStatementResult(statementResult);
                    });
            System.out.println(shortestPaths.stream().map(list -> list.stream().collect(Collectors.joining(" -> "))).collect(Collectors.joining("\n")));
        }
    }

    // TODO: Optimaliseerbaar door gebruik van volgende query: 'MATCH (s) WHERE ID(s) in [19, 3309035] RETURN ID(s),s.title' voor meerdere nodes
    private static final String nodeNameQuery = "MATCH (s) WHERE ID(s) = $id RETURN s.title";

    private String getNodeName(final long nodeId) {
        try (Session session = driver.session()) {
            String result = session.writeTransaction(tx -> {
                StatementResult result1 = tx.run(nodeNameQuery,
                        parameters("id", nodeId));
                return result1.single().get(0).asString();
            });
            return result;
        }
    }


    private HashSet<List<String>> extractPathsFromStatementResult(StatementResult result) {
        // Using a set to filter out duplicate paths (due to duplicate IDs for pages)
        HashSet<List<String>> paths = new HashSet<>();

        // For all found shortest paths
        for (Record rec : result.list()) {
            rec.asMap().forEach((key, value) ->
                    paths.add(convertPathToList((InternalPath) value)));
        }
        return paths;
    }

    private List<String> convertPathToList(InternalPath path) {
        return StreamSupport.stream(path.nodes().spliterator(), false).map(e -> getNodeName(e.id())).collect(Collectors.toList());
    }

    public static void main(String... args) throws Exception {
        LinksFinderArguments arguments = new LinksFinderArguments();

        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);


        WikipediaLinksFinder finder = new WikipediaLinksFinder(arguments.getDatabaseUrl(), arguments.getLogin(), arguments.getPassword());
        finder.findShortestPath(arguments.getFrom(), arguments.getTo());
    }
}
