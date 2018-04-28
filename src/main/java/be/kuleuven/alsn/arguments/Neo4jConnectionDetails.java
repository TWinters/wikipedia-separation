package be.kuleuven.alsn.arguments;

import com.beust.jcommander.Parameter;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

public class Neo4jConnectionDetails {
    @Parameter(names = "-db_url", description = "The location of the Neo4J graph database")
    private String databaseUrl;

    @Parameter(names = "-db_login", description = "Login name of the Neo4J graph database")
    private String login;

    @Parameter(names = "-db_pw", description = "Login password of the Neo4J graph database")
    private String password;

    public Neo4jConnectionDetails(String neo4jURI, String neo4jUsername, String neo4jPassword) {
        this.databaseUrl = neo4jURI;
        this.login = neo4jUsername;
        this.password = neo4jPassword;
    }

    public Neo4jConnectionDetails() {
        this("bolt://localhost:7687", "neo4j", "admin");
    }


    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Driver createConnection() {
        return GraphDatabase.driver(getDatabaseUrl(), AuthTokens.basic(getLogin(), getPassword()));
    }
}
