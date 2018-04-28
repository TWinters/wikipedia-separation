package be.kuleuven.alsn.analysers;

import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import org.neo4j.driver.v1.Driver;

import static org.neo4j.driver.v1.Values.parameters;

public class WikiPageExistanceChecker implements AutoCloseable {

    private final Driver driver;

    private WikiPageExistanceChecker(Driver driver) {
        this.driver = driver;
    }

    public WikiPageExistanceChecker(Neo4jConnectionDetails neo4jArguments) {
        this(neo4jArguments.createConnection());
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    private static final String existanceCheckerQuery = "MATCH (page:Page { title: $title }) RETURN page";

    public boolean isValidPage(String title) {
        return driver.session()
                .writeTransaction(tx ->
                        tx.run(existanceCheckerQuery, parameters("title", title)))
                .hasNext();
    }
}
