package be.kuleuven.alsn.data;

import java.util.Objects;

public class WikipediaPageCard {
    private final long pageId;
    private final String pageName;

    public WikipediaPageCard(long pageId, String pageName) {

        this.pageName = pageName;
        this.pageId = pageId;
    }

    public long getPageId() {
        return pageId;
    }

    public String getPageName() {
        return pageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WikipediaPageCard that = (WikipediaPageCard) o;
        return pageId == that.pageId &&
                Objects.equals(pageName, that.pageName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(pageId, pageName);
    }

    @Override
    public String toString() {
        return pageName + " (" + pageId + ")";
    }
}
