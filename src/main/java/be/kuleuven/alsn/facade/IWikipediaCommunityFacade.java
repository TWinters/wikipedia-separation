package be.kuleuven.alsn.facade;

import be.kuleuven.alsn.data.WikiCommunity;
import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageCard;

import java.util.Collection;
import java.util.function.Consumer;

public interface IWikipediaCommunityFacade {
    void addBlockListener(Consumer<WikiCommunityToken> listener);

    void addUnblockListener(Consumer<WikiCommunityToken> listener);

    WikiCommunityToken getCommunityOf(WikiPageCard page);

    WikiCommunity getCommunityPages(WikiCommunityToken communityId);

    Collection<WikiCommunityToken> getBlockedCommunities();

    void blockCommunity(WikiCommunityToken token);

    void unblockCommunity(WikiCommunityToken token);

    boolean isBlocked(WikiCommunityToken community);
}
