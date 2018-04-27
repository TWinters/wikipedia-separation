package be.kuleuven.alsn.gui;

import be.kuleuven.alsn.data.WikipediaPath;
import be.kuleuven.alsn.facade.IWikipediaSeperationFacade;

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
        new WikipediaSeperationGUI(null).run();
    }
    //endregion
}
