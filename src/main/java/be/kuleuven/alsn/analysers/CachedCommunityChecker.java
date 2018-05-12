package be.kuleuven.alsn.analysers;

import be.kuleuven.alsn.data.WikiCommunity;
import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageCard;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ExecutionException;

public class CachedCommunityChecker implements IWikiCommunityChecker {

    private IWikiCommunityChecker communityChecker;

    public CachedCommunityChecker(IWikiCommunityChecker communityChecker) {
        this.communityChecker = communityChecker;
    }

    private Cache<WikiPageCard, WikiCommunityToken> communityCache = CacheBuilder.newBuilder().softValues().build();
    @Override
    public WikiCommunityToken getCommunityOf(WikiPageCard page) {
        try {
            return communityCache.get(page, ()->communityChecker.getCommunityOf(page));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Cache<WikiCommunityToken, WikiCommunity> pagesCache = CacheBuilder.newBuilder().softValues().build();
    @Override
    public WikiCommunity getCommunityPages(WikiCommunityToken communityId) {
        try {
            return pagesCache.get(communityId, ()->communityChecker.getCommunityPages(communityId));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
