package be.kuleuven.alsn.arguments;

import be.kuleuven.alsn.data.WikiPageCommunityToken;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Arguments that are parsed by JCommander
 * See http://jcommander.org/ for more information
 */
public class LinksFinderArguments {

    @Parameter(names = "-from", description = "The wikipedia page to start from")
    private String from = "Katholieke_Universiteit_Leuven";

    @Parameter(names = "-to", description = "The goal wikipedia page to end on")
    private String to = "Adolf_Hitler";

    @Parameter(names = "-blocked", description = "Communities of which nodes are not allowed to appear on the path")
    private Collection<WikiPageCommunityToken> blockedCommunities = new ArrayList<>();


    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Collection<WikiPageCommunityToken> getBlockedCommunities() {
        return blockedCommunities;
    }
}
