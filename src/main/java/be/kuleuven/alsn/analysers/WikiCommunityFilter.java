package be.kuleuven.alsn.analysers;

import be.kuleuven.alsn.data.WikiCommunityToken;

import java.util.*;
import java.util.function.Consumer;

public class WikiCommunityFilter {
    private final Set<WikiCommunityToken> blockedCommunities = new LinkedHashSet<>();
    private final List<Consumer<WikiCommunityToken>> blockListeners = new ArrayList<>();
    private final List<Consumer<WikiCommunityToken>> unblockListeners = new ArrayList<>();

    public void block(WikiCommunityToken community) {
        blockedCommunities.add(community);
        blockListeners.parallelStream().forEach(e->e.accept(community));
    }

    public void unblock(WikiCommunityToken community) {
        blockedCommunities.remove(community);
        unblockListeners.parallelStream().forEach(e->e.accept(community));
    }

    public Collection<WikiCommunityToken> getBlockedCommunities() {
        return blockedCommunities;
    }

    public boolean isBlocked(WikiCommunityToken community) {
        return blockedCommunities.contains(community);
    }

    public void addBlockListener(Consumer<WikiCommunityToken> listener) {
        blockListeners.add(listener);
    }
    public void addUnblockListener(Consumer<WikiCommunityToken> listener) {
        unblockListeners.add(listener);
    }
}
