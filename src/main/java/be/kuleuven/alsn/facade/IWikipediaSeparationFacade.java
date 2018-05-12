package be.kuleuven.alsn.facade;

import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiPageCommunityToken;
import be.kuleuven.alsn.data.WikiPath;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IWikipediaSeparationFacade {

    default void setNeo4jConnection(String neo4jURI, String neo4jUsername, String neo4jPassword) {
        this.setNeo4jConnection(new Neo4jConnectionDetails(neo4jURI, neo4jUsername, neo4jPassword));
    }

    void setNeo4jConnection(Neo4jConnectionDetails neo4jArguments);

    Optional<Neo4jConnectionDetails> getNeo4JConnectDetails();

    List<Long> getClusters();

    Collection<WikiPath> calculateShortestPath(String from, String to,
                                               Collection<WikiPageCommunityToken> blockedCommunities);

    boolean isValidPage(String page);
}
