package be.kuleuven.alsn.arguments;

import com.beust.jcommander.Parameter;

public class Neo4jConnectionDetails {
    @Parameter(names = "-db_url", description = "The location of the Neo4J graph database")
    private String databaseUrl = "bolt://localhost:7687";

    @Parameter(names = "-db_login", description = "Login name of the Neo4J graph database")
    private String login = "neo4j";

    @Parameter(names = "-db_pw", description = "Login password of the Neo4J graph database")
    private String password = "admin";


    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
