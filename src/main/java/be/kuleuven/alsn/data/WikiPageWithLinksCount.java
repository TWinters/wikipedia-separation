package be.kuleuven.alsn.data;

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
}