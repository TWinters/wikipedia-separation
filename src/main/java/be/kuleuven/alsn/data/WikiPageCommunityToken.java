package be.kuleuven.alsn.data;

public class WikiPageCommunityToken {
    private final long id;

    public WikiPageCommunityToken(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Community{" + id + '}';
    }

    public long getId() {
        return id;
    }
}
