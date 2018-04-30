package be.kuleuven.alsn.data;

public class WikiPageCommunityToken {
    private final int id;

    public WikiPageCommunityToken(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Community{" + id + '}';
    }
}
