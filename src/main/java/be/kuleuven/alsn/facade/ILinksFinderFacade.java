package be.kuleuven.alsn.facade;

import java.util.List;

public interface ILinksFinderFacade {

    List<Long> getClusters();

    List<String> calculateShortestPath(String from, String to);

    boolean isValidPage(String page);
}
