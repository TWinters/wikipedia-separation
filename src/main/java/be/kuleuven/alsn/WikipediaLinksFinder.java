package be.kuleuven.alsn;

import com.google.protobuf.Internal;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.driver.v1.*;

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

    private static final String shortestPathQuery ="MATCH (begin:Page { title: $from }),(end:Page { title: $to }), p = shortestPath((begin)-[:REFERENCES_TO*]->(end)) RETURN p";

    public void findShortestPath(final String from, final String to) {
        try (Session session = driver.session()) {
            String result = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    StatementResult result = tx.run(shortestPathQuery,
                            parameters("from", from, "to", to));


                    return printTransactionResult(result);
                }
            });
            System.out.println(result);
        }
    }

    private static final String nodeNameQuery = "MATCH (s) WHERE ID(s) = $id RETURN s.title";
    public String getNodeName(final long nodeId) {
        try (Session session = driver.session()) {
            String result = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    StatementResult result = tx.run(nodeNameQuery,
                            parameters("id", nodeId));


                    return result.single().get(0).asString();
                }
            });
            return result;
        }
    }


    public String printTransactionResult(StatementResult result) {
        StringBuilder b = new StringBuilder();
        for (Record rec : result.list()) {
            rec.asMap().forEach((key, value) -> {
                b.append(convertPathToList((InternalPath) value).stream().collect(Collectors.joining(" -> "))+"\n");
            });
        }
        return b.toString();
    }

    public List<String> convertPathToList(InternalPath path) {
        return StreamSupport.stream(path.nodes().spliterator(),false).map(e->getNodeName(e.id())).collect(Collectors.toList());
    }

    public static void main(String... args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Please provide a login for the neo4j database as command line arguments, in the format [login-name] [password]");
        }

        WikipediaLinksFinder finder = new WikipediaLinksFinder("bolt://localhost:7687", args[0], args[1]);
        finder.findShortestPath("Yoctogram", "Adolf_Hitler");
    }
}
