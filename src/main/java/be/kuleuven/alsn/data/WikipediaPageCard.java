package be.kuleuven.alsn.data;

public class WikipediaPageCard {
    private final String pageName;
    private final long pageId;

    public WikipediaPageCard(String pageName, long pageId) {

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
