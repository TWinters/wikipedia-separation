package be.kuleuven.alsn.gui;

import be.kuleuven.alsn.arguments.LinksFinderArguments;
import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiPath;
import be.kuleuven.alsn.facade.IWikipediaSeperationFacade;
import be.kuleuven.alsn.facade.WikipediaSeperationFacade;
import com.beust.jcommander.JCommander;
import org.neo4j.driver.v1.exceptions.AuthenticationException;

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
    private JTabbedPane tabbedPane1;
    private JTextField txtNeo4jUsername;
    private JTextField txtNeo4jURI;
    private JPasswordField txtNeo4jPassword;
    private JButton updateConnectionButton;

    //region Initialisation
    private final IWikipediaSeperationFacade facade;

    public WikipediaSeperationGUI(IWikipediaSeperationFacade facade) {
        this.facade = facade;

        // Setting connections
        facade.getNeo4JConnectDetails().ifPresent(this::setNeo4jConnection);

        // Linking buttons
        btnCalculateShortestPath.addActionListener(x -> calculateShortestPath());
        btnToBlacklist.addActionListener(x -> addSelectionToBlacklist());
        btnToWhiteList.addActionListener(x -> addSelectionToWhitelist());
        updateConnectionButton.addActionListener(x -> updateNeo4jConnection(true));
    }

    private void setDefaultLinkArguments(LinksFinderArguments linkArguments) {
        this.txtFrom.setText(linkArguments.getFrom());
        this.txtTo.setText(linkArguments.getTo());
    }

    //endregion


    //region Calculating shortest path
    private void calculateShortestPath() {
        String from = txtFrom.getText();
        String to = txtTo.getText();

        Collection<WikiPath> paths = facade.calculateShortestPath(from, to);

        System.out.println("Found paths: " + paths);

//        // test code
//        paths = Collections.singleton(new WikiPath(
//                Arrays.asList(
//                        new WikiPageCard(3, "Dit"),
//                        new WikiPageCard(1, "is"),
//                        new WikiPageCard(4, "een"),
//                        new WikiPageCard(5, "Test")
//                )
//        ));

        // Open a window for each path
        paths.stream().map(WikipediaPathViewer::new).forEach(WikipediaPathViewer::run);
    }
    //end region


    //region Cluster filtering
    private void addSelectionToWhitelist() {
        // TODO
    }

    private void addSelectionToBlacklist() {
        //TODO
    }


    //region updating Neo4J connection
    private void setNeo4jConnection(Neo4jConnectionDetails neo4jArguments) {
        this.txtNeo4jURI.setText(neo4jArguments.getDatabaseUrl());
        this.txtNeo4jUsername.setText(neo4jArguments.getLogin());
        this.txtNeo4jPassword.setText(neo4jArguments.getPassword());
        updateNeo4jConnection(false);
    }

    private void updateNeo4jConnection(boolean showSuccessDialog) {
        try {
            facade.setNeo4jConnection(txtNeo4jURI.getText(), txtNeo4jUsername.getText(), new String(txtNeo4jPassword.getPassword()));
            if (showSuccessDialog) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Successfully connected to Neo4J!",
                        "Neo4J connection successful",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (AuthenticationException authEx) {
            JOptionPane.showMessageDialog(mainPanel,
                    "Could not connect to the Neo4J server using the provided details",
                    "Neo4J connection error",
                    JOptionPane.ERROR_MESSAGE);
        }
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

        IWikipediaSeperationFacade facade = new WikipediaSeperationFacade();
        facade.setNeo4jConnection(neo4jArguments);

        WikipediaSeperationGUI gui = new WikipediaSeperationGUI(facade);
        gui.setDefaultLinkArguments(linkArguments);
        gui.run();
    }
    //endregion
}