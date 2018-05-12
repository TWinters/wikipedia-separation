package be.kuleuven.alsn.analysers;

import be.kuleuven.alsn.data.WikiCommunity;
import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageCard;

public interface IWikiCommunityChecker {
    WikiCommunityToken getCommunityOf(WikiPageCard page);

    WikiCommunity getCommunityPages(WikiCommunityToken communityId);
}
