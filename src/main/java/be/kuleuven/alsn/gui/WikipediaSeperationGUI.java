package be.kuleuven.alsn.gui;

import be.kuleuven.alsn.arguments.LinksFinderArguments;
import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikipediaPath;
import be.kuleuven.alsn.facade.IWikipediaSeperationFacade;
import be.kuleuven.alsn.facade.WikipediaSeperationFacade;
import com.beust.jcommander.JCommander;

import javax.swing.*;
import java.util.Collection;

public class WikipediaSeperationGUI {
    private JTextField txtFrom;
    private JTextField txtTo;
    private JList lstClusterWhitelist;
    private JList lstClusterBlacklist;
    private JButton btnToBlacklist;
    private JButton btnToWhiteList;
    private JButton btnCalculateShortestPath;
    private JPanel mainPanel;
    private JTextField txtNeo4jUsername;
    private JTextField txtNeo4jURI;
    private JPasswordField txtNeo4jPassword;
    private JButton updateConnectionButton;

    //region Initialisation
    private final IWikipediaSeperationFacade facade;

    public WikipediaSeperationGUI(IWikipediaSeperationFacade facade) {
        this.facade = facade;
        btnCalculateShortestPath.addActionListener(x -> calculateShortestPath());
        btnToBlacklist.addActionListener(x -> addSelectionToBlacklist());
        btnToWhiteList.addActionListener(x -> addSelectionToWhitelist());
        updateConnectionButton.addActionListener(x -> updateNeo4jConnection());
    }

    private void setDefaultLinkArguments(LinksFinderArguments linkArguments) {
        this.txtFrom.setText(linkArguments.getFrom());
        this.txtTo.setText(linkArguments.getTo());
    }

    private void setNeo4jConnection(Neo4jConnectionDetails neo4jArguments) {
        this.txtNeo4jURI.setText(neo4jArguments.getDatabaseUrl());
        this.txtNeo4jUsername.setText(neo4jArguments.getLogin());
        this.txtNeo4jPassword.setText(neo4jArguments.getPassword());
        updateNeo4jConnection();
    }
    //endregion

    //region Neo4J connection

    private void updateNeo4jConnection() {
        facade.updateNeo4jConnection(txtNeo4jURI.getText(), txtNeo4jUsername.getText(), new String(txtNeo4jPassword.getPassword()));
    }
    //endregion

    //region Calculating shortest path
    private void calculateShortestPath() {
        String from = txtFrom.getText();
        String to = txtTo.getText();

        Collection<WikipediaPath> path = facade.calculateShortestPath(from, to);


    }
    //end region


    //region Cluster filtering
    private void addSelectionToWhitelist() {
        //TODO
    }

    private void addSelectionToBlacklist() {
        //TODO
    }
    //endregion


    //region Running the GUI
    public void run() {
        JFrame frame = new JFrame("Wikipedia Seperation GUI");
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        Neo4jConnectionDetails neo4jArguments = new Neo4jConnectionDetails();
        LinksFinderArguments linkArguments = new LinksFinderArguments();

        JCommander.newBuilder()
                .addObject(neo4jArguments)
                .addObject(linkArguments)
                .build()
                .parse(args);

        WikipediaSeperationGUI gui = new WikipediaSeperationGUI(new WikipediaSeperationFacade());
        gui.setNeo4jConnection(neo4jArguments);
        gui.setDefaultLinkArguments(linkArguments);
        gui.run();
    }

    //endregion
}
