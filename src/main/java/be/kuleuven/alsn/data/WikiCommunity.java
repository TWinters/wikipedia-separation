package be.kuleuven.alsn.data;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class WikiCommunity {
    private final WikiCommunityToken token;
    private final ImmutableList<WikiPageWithLinksCount> pageCards;

    public WikiCommunity(WikiCommunityToken token, Collection<WikiPageWithLinksCount> pageCards) {
        this.token = token;
        this.pageCards = ImmutableList.copyOf(pageCards);
    }

    public ImmutableList<WikiPageWithLinksCount> getPageCards() {
        return pageCards;
    }

    public WikiCommunityToken getToken() {

        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WikiCommunity that = (WikiCommunity) o;
        return Objects.equals(token, that.token) &&
                Objects.equals(pageCards, that.pageCards);
    }

    @Override
    public int hashCode() {

        return Objects.hash(token, pageCards);
    }

    @Override
    public String toString() {
        return pageCards.subList(0, Math.max(0,Math.min(3,pageCards.size())))
                .stream()
                .map(WikiPageWithLinksCount::getCard)
                .map(WikiPageCard::getPageName)
                .collect(Collectors.joining(", "))
                + (pageCards.size()>3 ? "..." : "");
    }
}
