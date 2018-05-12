package be.kuleuven.alsn.data;

public class WikiCommunityToken {
    private final long id;

    public WikiCommunityToken(long id) {
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
