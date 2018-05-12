package be.kuleuven.alsn.facade;

import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPageWithLinksCount;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface IWikipediaCommunityFacade {
    void addBlockListener(Consumer<WikiCommunityToken> listener);

    void addUnblockListener(Consumer<WikiCommunityToken> listener);

    WikiCommunityToken getCommunityOf(WikiPageCard page);

    List<WikiPageWithLinksCount> getCommunityPages(WikiCommunityToken communityId);

    Collection<WikiCommunityToken> getBlockedCommunities();

    void blockCommunity(WikiCommunityToken token);

    void unblockCommunity(WikiCommunityToken token);

    boolean isBlocked(WikiCommunityToken community);
}
