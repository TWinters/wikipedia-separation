package be.kuleuven.alsn.facade;

import be.kuleuven.alsn.analysers.WikiCommunityChecker;
import be.kuleuven.alsn.analysers.WikiCommunityFilter;
import be.kuleuven.alsn.analysers.WikiPageExistanceChecker;
import be.kuleuven.alsn.analysers.WikiPathFinder;
import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPageWithLinksCount;
import be.kuleuven.alsn.data.WikiPath;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class WikipediaSeparationFacade implements IWikipediaSeparationFacade {
    private WikiPathFinder linksFinder;
    private WikiCommunityChecker communityChecker;
    private WikiCommunityFilter communityFilter = new WikiCommunityFilter();
    private WikiPageExistanceChecker existanceChecker;
    private Neo4jConnectionDetails neo4jConnectionDetails;

    @Override
    public Collection<WikiPath> calculateShortestPath(String from, String to,
                                                      Collection<WikiCommunityToken> blockedCommunities) {
        return linksFinder.findShortestPath(from, to, blockedCommunities);
    }

    @Override
    public boolean isValidPage(String page) {
        return existanceChecker.isValidPage(page);
    }

    //region Neo4J Connection
    @Override
    public void setNeo4jConnection(Neo4jConnectionDetails neo4jArguments) {
        this.neo4jConnectionDetails = neo4jArguments;
        this.linksFinder = new WikiPathFinder(neo4jArguments);
        this.communityChecker = new WikiCommunityChecker(neo4jArguments);
        this.existanceChecker = new WikiPageExistanceChecker(neo4jArguments);
    }

    @Override
    public Optional<Neo4jConnectionDetails> getNeo4JConnectDetails() {
        return Optional.ofNullable(neo4jConnectionDetails);
    }


    @Override
    public WikiCommunityToken getCommunityOf(WikiPageCard page) {
        return communityChecker.getCommunityOf(page);
    }

    @Override
    public List<WikiPageWithLinksCount> getCommunityPages(WikiCommunityToken communityId) {
        return communityChecker.getCommunityPages(communityId);
    }

    @Override
    public Collection<WikiCommunityToken> getBlockedCommunities() {
        return communityFilter.getBlockedCommunities();
    }

    @Override
    public void blockCommunity(WikiCommunityToken token) {
        communityFilter.block(token);
    }

    @Override
    public void unblockCommunity(WikiCommunityToken token) {
        communityFilter.unblock(token);
    }

    @Override
    public boolean isBlocked(WikiCommunityToken community) {
        return communityFilter.isBlocked(community);
    }

    @Override
    public void addBlockListener(Consumer<WikiCommunityToken> listener) {
        communityFilter.addBlockListener(listener);
    }

    @Override
    public void addUnblockListener(Consumer<WikiCommunityToken> listener) {
        communityFilter.addUnblockListener(listener);
    }
    /* endregion */
}
