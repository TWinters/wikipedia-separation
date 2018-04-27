package be.kuleuven.alsn.facade;

import be.kuleuven.alsn.WikipediaLinksFinder;
import be.kuleuven.alsn.data.WikipediaPath;

import java.util.Collection;
import java.util.List;

public class WikipediaSeperationFacade implements IWikipediaSeperationFacade {
    private WikipediaLinksFinder linksFinder;

    public WikipediaSeperationFacade(String neo4jURI, String neo4jUsername, String neo4jPassword) {
        updateNeo4jConnection(neo4jURI, neo4jUsername, neo4jPassword);
    }

    @Override
    public void updateNeo4jConnection(String neo4jURI, String neo4jUsername, String neo4jPassword) {
        this.linksFinder = new WikipediaLinksFinder(neo4jURI, neo4jUsername, neo4jPassword);
    }

    @Override
    public List<Long> getClusters() {
        return null;
    }

    @Override
    public Collection<WikipediaPath> calculateShortestPath(String from, String to) {
        return linksFinder.findShortestPath(from, to);
    }

    @Override
    public boolean isValidPage(String page) {
        return false;
    }
}
