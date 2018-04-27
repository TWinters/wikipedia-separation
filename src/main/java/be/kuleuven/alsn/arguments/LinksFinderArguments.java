package be.kuleuven.alsn.arguments;

import com.beust.jcommander.Parameter;

/**
 * Arguments that are parsed by JCommander
 * See http://jcommander.org/ for more information
 */
public class LinksFinderArguments {

    @Parameter(names = "-from", description = "The wikipedia page to start from")
    private String from = "Katholieke_Universiteit_Leuven";

    @Parameter(names = "-to", description = "The goal wikipedia page to end on")
    private String to = "Adolf_Hitler";


    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
