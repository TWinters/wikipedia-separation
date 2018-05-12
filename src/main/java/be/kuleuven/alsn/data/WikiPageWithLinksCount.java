package be.kuleuven.alsn.data;

import java.util.Objects;

public class WikiPageWithLinksCount {
    private final WikiPageCard card;
    private final int incomingLinks;

    public WikiPageWithLinksCount(WikiPageCard card, int incomingLinks) {
        this.card = card;
        this.incomingLinks = incomingLinks;
    }

    public WikiPageCard getCard() {
        return card;
    }

    public int getIncomingLinks() {
        return incomingLinks;
    }

    @Override
    public String toString() {
        return card.toString() + " (" + incomingLinks+ ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WikiPageWithLinksCount that = (WikiPageWithLinksCount) o;
        return incomingLinks == that.incomingLinks &&
                Objects.equals(card, that.card);
    }

    @Override
    public int hashCode() {

        return Objects.hash(card, incomingLinks);
    }
}