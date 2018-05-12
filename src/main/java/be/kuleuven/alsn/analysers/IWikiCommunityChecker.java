package be.kuleuven.alsn.analysers;

import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPageWithLinksCount;

import java.util.List;

public interface IWikiCommunityChecker {
    WikiCommunityToken getCommunityOf(WikiPageCard page);

    List<WikiPageWithLinksCount> getCommunityPages(WikiCommunityToken communityId);
}
