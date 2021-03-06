package be.kuleuven.alsn.data;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WikiPath {
    private final ImmutableList<WikiPageCard> pages;

    public WikiPath(List<WikiPageCard> pages) {
        this.pages = ImmutableList.copyOf(pages);
    }

    public List<WikiPageCard> getPages() {
        return pages;
    }


    public int getPathLength() {
        return pages.size();
    }

    public WikiPageCard getPage(int index) {
        return pages.get(index);
    }

    @Override
    public String toString() {
        return pages.stream()
                .map(WikiPageCard::getPageName)
                .collect(Collectors.joining(" -> "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WikiPath that = (WikiPath) o;
        return Objects.equals(pages, that.pages);
    }

    @Override
    public int hashCode() {

        return Objects.hash(pages);
    }
}
