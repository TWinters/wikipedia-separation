package be.kuleuven.alsn.arguments;

import com.beust.jcommander.Parameter;

/**
 * Arguments that are parsed by JCommander
 * See http://jcommander.org/ for more information
 */
public class LinksFinderArguments {

    @Parameter(names = "-db_url", description = "The location of the Neo4J graph database")
    private String databaseUrl = "bolt://localhost:7687";

    @Parameter(names = "-db_login", description = "Login name of the Neo4J graph database")
    private String login = "neo4j";

    @Parameter(names = "-db_pw", description = "Login password of the Neo4J graph database")
    private String password = "admin";

    @Parameter(names = "-from", description = "The wikipedia page to start from")
    private String from = "Katholieke_Universiteit_Leuven";

    @Parameter(names = "-to", description = "The goal wikipedia page to end on")
    private String to = "Adolf_Hitler";


    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
