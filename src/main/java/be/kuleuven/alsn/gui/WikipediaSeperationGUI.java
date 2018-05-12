package be.kuleuven.alsn.gui;

import be.kuleuven.alsn.arguments.LinksFinderArguments;
import be.kuleuven.alsn.arguments.Neo4jConnectionDetails;
import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPath;
import be.kuleuven.alsn.facade.IWikipediaSeparationFacade;
import be.kuleuven.alsn.facade.WikipediaSeparationFacade;
import com.beust.jcommander.JCommander;
import org.neo4j.driver.v1.exceptions.AuthenticationException;

import javax.swing.*;
import java.util.Collection;

public class WikipediaSeperationGUI {
    private JTextField txtFrom;
    private JTextField txtTo;
    private JList<WikiCommunityToken> lstClusterBlacklist;
    private JButton btnToWhiteList;
    private JButton btnCalculateShortestPath;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JTextField txtNeo4jUsername;
    private JTextField txtNeo4jURI;
    private JPasswordField txtNeo4jPassword;
    private JButton updateConnectionButton;
    private JButton viewCommunityButton;

    //region Initialisation
    private final IWikipediaSeparationFacade facade;

    public WikipediaSeperationGUI(IWikipediaSeparationFacade facade) {
        this.facade = facade;

        // Setting connections
        facade.getNeo4JConnectDetails().ifPresent(this::setNeo4jConnection);

        // Linking buttons
        btnCalculateShortestPath.addActionListener(x -> calculateShortestPath());
        updateConnectionButton.addActionListener(x -> updateNeo4jConnection(true));

        btnToWhiteList.addActionListener(x -> addSelectionToWhitelist());
        facade.addBlockListener(x->updateBlockedCommunities());
        facade.addUnblockListener(x->updateBlockedCommunities());
        updateBlockedCommunities();

        initialiseFrame();
        viewCommunityButton.addActionListener(x->openSelectedCommunities());
    }


    private void setDefaultLinkArguments(LinksFinderArguments linkArguments) {
        this.txtFrom.setText(linkArguments.getFrom());
        this.txtTo.setText(linkArguments.getTo());
    }

    //endregion


    private void updateBlockedCommunities() {
        DefaultListModel<WikiCommunityToken> defaultListModel = new DefaultListModel<>();
        facade.getBlockedCommunities().forEach(defaultListModel::addElement);
        lstClusterBlacklist.setModel(defaultListModel);
    }


    //region Calculating shortest path
    private void calculateShortestPath() {
        String from = txtFrom.getText();
        String to = txtTo.getText();

        btnCalculateShortestPath.setEnabled(false);
        new Thread(() -> {
            if (checkValidnessWithDialog(from) && checkValidnessWithDialog(to)) {
                Collection<WikiPath> paths = facade.calculateShortestPath(from, to);

                if (paths.isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel,
                            "There is no path found between '" + from + "' and '" + to + "'.",
                            "No paths found",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Open a window for each path
                    paths.stream()
                            .peek(System.out::println)
                            .map(path-> new WikipediaPathViewer(facade, path))
                            .forEach(WikipediaPathViewer::run);
                }
            }
            btnCalculateShortestPath.setEnabled(true);
        }).start();
    }
    //end region

    private boolean checkValidnessWithDialog(String page) {
        if (!facade.isValidPage(page)) {
            JOptionPane.showMessageDialog(mainPanel,
                    "The given page '" + page + "' does not exist.",
                    "Neo4J non-existing page error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    //region Cluster filtering
    private void addSelectionToWhitelist() {
        lstClusterBlacklist.getSelectedValuesList().forEach(facade::unblockCommunity);
    }

    private void openSelectedCommunities() {
        lstClusterBlacklist.getSelectedValuesList().forEach(community->new CommunityViewer(facade, community));
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
    public void initialiseFrame() {
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

        IWikipediaSeparationFacade facade = new WikipediaSeparationFacade();
        facade.setNeo4jConnection(neo4jArguments);

        WikipediaSeperationGUI gui = new WikipediaSeperationGUI(facade);
        gui.setDefaultLinkArguments(linkArguments);
    }
    //endregion
}
