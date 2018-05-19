package be.kuleuven.alsn.facade;

import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPath;

import java.util.Collection;
import java.util.Optional;

public interface IWikipediaSeparationFacade extends IWikipediaCommunityFacade {

    default void setNeo4jConnection(String neo4jURI, String neo4jUsername, String neo4jPassword) {
        this.setNeo4jConnection(new Neo4jConnectionDetails(neo4jURI, neo4jUsername, neo4jPassword));
    }

    void setNeo4jConnection(Neo4jConnectionDetails neo4jArguments);

    Optional<Neo4jConnectionDetails> getNeo4JConnectDetails();


    Collection<WikiPath> calculateShortestPath(String from, String to);

    boolean isValidPage(String page);

    WikiPageCard getRandomPage();

    void setPageRandomiserSeed(Long aLong);
}
