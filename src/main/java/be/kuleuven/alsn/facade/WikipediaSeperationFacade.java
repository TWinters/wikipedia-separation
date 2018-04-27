package be.kuleuven.alsn.facade;

import be.kuleuven.alsn.WikipediaLinksFinder;
import be.kuleuven.alsn.data.WikipediaPath;

import java.util.Collection;
import java.util.List;

public class WikipediaSeperationFacade implements IWikipediaSeperationFacade {
    private final WikipediaLinksFinder linksFinder = new WikipediaLinksFinder();

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
