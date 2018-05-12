package be.kuleuven.alsn.data;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WikiCommunityToken that = (WikiCommunityToken) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
