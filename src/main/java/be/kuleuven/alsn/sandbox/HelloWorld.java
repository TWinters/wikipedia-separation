package be.kuleuven.alsn.sandbox;

import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

public class HelloWorld implements AutoCloseable {
    private final Driver driver;

    public HelloWorld(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public void printGreeting(final String message) {
        try (Session session = driver.session()) {
            String greeting = session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    StatementResult result = tx.run("CREATE (a:Greeting) " +
                                    "SET a.message = $message " +
                                    "RETURN a.message + ', from node ' + id(a)",
                            parameters("message", message));
                    return result.single().get(0).asString();
                }
            });
            System.out.println(greeting);
        }
    }

    public static void connect(String user, String password) throws Exception {
//        SparkConf conf = new SparkConf()
//                .setAppName("HelloWorldTestApp")
//                .setMaster("local[*]")
//                .set("spark.driver.allowMultipleContexts", "true")
//                .set("spark.neo4j.bolt.user", user)
//                .set("spark.neo4j.bolt.password", password)
//                .set("spark.neo4j.bolt.url", "bolt://host:7687");
//        JavaSparkContext sc = new JavaSparkContext(conf);
//        Neo4JavaSparkContext csc = Neo4JavaSparkContext.neo4jContext(sc);
//        JavaRDD<Map<String, Object>> found = csc.query("MATCH (n:Page) RETURN n LIMIT 25", new HashMap<>());
//        System.out.println("RESULT: " + found.collect().stream().map(Object::toString).collect(Collectors.joining("\n")));

//        Neo4jGraph.loadGraph(sc.sc(), "MATCH (n:Page) RETURN n LIMIT 25", )
        HelloWorld greeter = new HelloWorld("bolt://localhost:7687", user, password);
        greeter.printGreeting("Hello, world");

    }

    public static void main(String... args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException("Please provide a login for the neo4j database as command line arguments, in the format [login-name] [password]");
        }

        connect(args[0], args[1]);
    }
}