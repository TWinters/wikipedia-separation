package be.kuleuven.alsn.data;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class WikipediaPath {
    private final ImmutableList<WikipediaPageCard> pages;

    public WikipediaPath(List<WikipediaPageCard> pages) {
        this.pages = ImmutableList.copyOf(pages);
    }

    public List<WikipediaPageCard> getPages() {
        return pages;
    }
}
