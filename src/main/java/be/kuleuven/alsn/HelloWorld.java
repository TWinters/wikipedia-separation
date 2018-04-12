package be.kuleuven.alsn;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
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

    public static void main(String... args) throws Exception {
        String user = "neo4j";
        String password = "password";
        try (HelloWorld greeter = new HelloWorld("bolt://localhost:7687", user, password)) {
            greeter.printGreeting("hello, world");
        }

        SparkConf conf = new SparkConf();
        conf.setAppName("HelloWorld");
        //conf.set("spark.neo4j.bolt.password=<password>", "password");
        conf.setMaster("bolt://host:7687");
        conf.set("spark.neo4j.bolt.user", user);
        conf.set("spark.neo4j.bolt.password", password);
        JavaSparkContext sc = new JavaSparkContext(conf);
    }
}
