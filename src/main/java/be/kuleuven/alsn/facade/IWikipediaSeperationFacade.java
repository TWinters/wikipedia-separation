package be.kuleuven.alsn.facade;

import be.kuleuven.alsn.data.WikipediaPath;

import java.util.Collection;
import java.util.List;

public interface IWikipediaSeperationFacade {

    void updateNeo4jConnection(String neo4jURI, String neo4jUsername, String neo4jPassword);

    List<Long> getClusters();

    Collection<WikipediaPath> calculateShortestPath(String from, String to);

    boolean isValidPage(String page);
}
