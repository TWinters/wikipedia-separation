package be.kuleuven.alsn.analysers;

import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiPageCommunityToken;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.StatementResult;

import static org.neo4j.driver.v1.Values.parameters;

public class WikiClusterFinder implements AutoCloseable {

    private final Driver driver;

    private WikiClusterFinder(Driver driver) {
        this.driver = driver;
    }

    public WikiClusterFinder(Neo4jConnectionDetails neo4jArguments) {
        this(neo4jArguments.createConnection());
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    private static final String GET_CLUSTER_OF_PAGE = "MATCH (page:Page{id: $id})-[:PART_OF_COM]->(c) RETURN c";

    public WikiPageCommunityToken getCommunityOf(long pageId) {
        StatementResult statementResult =
                driver.session()
                        .writeTransaction(tx ->
                                tx.run(GET_CLUSTER_OF_PAGE, parameters("id", pageId)));
        if (statementResult.hasNext()) {
            return new WikiPageCommunityToken(statementResult.single().get(0).asLong());
        } else {
            throw new IllegalArgumentException("No page with page id " + pageId + " exists.");
        }
    }

    
}
