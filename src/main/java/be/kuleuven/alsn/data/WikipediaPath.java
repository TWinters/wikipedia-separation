package be.kuleuven.alsn.data;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

public class WikipediaPath {
    private final ImmutableList<WikipediaPageCard> pages;

    public WikipediaPath(List<WikipediaPageCard> pages) {
        this.pages = ImmutableList.copyOf(pages);
    }

    public List<WikipediaPageCard> getPages() {
        return pages;
    }

    @Override
    public String toString() {
        return pages.stream()
                .map(WikipediaPageCard::getPageName)
                .collect(Collectors.joining(" -> "));
    }
}
