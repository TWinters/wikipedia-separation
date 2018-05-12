package be.kuleuven.alsn.analysers;

import be.kuleuven.alsn.data.WikiCommunityToken;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class WikiCommunityFilter {
    private final Set<WikiCommunityToken> blockedCommunities = new LinkedHashSet<>();

    public void block(WikiCommunityToken community) {
        blockedCommunities.add(community);
    }

    public void unblock(WikiCommunityToken community) {
        blockedCommunities.remove(community);
    }

    public Collection<WikiCommunityToken> getBlockedCommunities() {
        return blockedCommunities;
    }

    public boolean isBlocked(WikiCommunityToken community) {
        return blockedCommunities.contains(community);
    }
}
