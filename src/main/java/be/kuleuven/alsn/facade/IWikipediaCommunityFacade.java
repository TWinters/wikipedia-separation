package be.kuleuven.alsn.facade;

import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageWithLinksCount;

import java.util.Collection;
import java.util.List;

public interface IWikipediaCommunityFacade {
    List<WikiPageWithLinksCount> getCommunityPages(long communityId);

    Collection<WikiCommunityToken> getBlockedCommunities();

    void blockCommunity(WikiCommunityToken token);

    void unblockCommunity(WikiCommunityToken token);
}
