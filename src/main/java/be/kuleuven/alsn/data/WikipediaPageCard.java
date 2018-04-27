package be.kuleuven.alsn.data;

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

}
