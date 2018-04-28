package be.kuleuven.alsn.facade;

import be.kuleuven.alsn.analysers.WikiPathFinder;
import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiPath;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class WikipediaSeparationFacade implements IWikipediaSeparationFacade {
    private WikiPathFinder linksFinder;
    private Neo4jConnectionDetails neo4jConnectionDetails;

    @Override
    public List<Long> getClusters() {
        return null;
    }

    @Override
    public Collection<WikiPath> calculateShortestPath(String from, String to) {
        return linksFinder.findShortestPath(from, to);
    }

    @Override
    public boolean isValidPage(String page) {
        return false;
    }

    //region Neo4J Connection
    @Override
    public void setNeo4jConnection(Neo4jConnectionDetails neo4jArguments) {
        this.neo4jConnectionDetails = neo4jArguments;
        this.linksFinder = new WikiPathFinder(neo4jArguments);

    }

    @Override
    public Optional<Neo4jConnectionDetails> getNeo4JConnectDetails() {
        return Optional.ofNullable(neo4jConnectionDetails);
    }

    //endregion
}
