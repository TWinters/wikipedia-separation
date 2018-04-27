package be.kuleuven.alsn.facade;

import be.kuleuven.alsn.data.WikipediaPath;

import java.util.Collection;
import java.util.List;

public interface IWikipediaSeperationFacade {

    List<Long> getClusters();

    Collection<WikipediaPath> calculateShortestPath(String from, String to);

    boolean isValidPage(String page);
}
