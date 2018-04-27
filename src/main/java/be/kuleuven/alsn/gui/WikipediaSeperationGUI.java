package be.kuleuven.alsn.gui;

import be.kuleuven.alsn.facade.ILinksFinderFacade;

import javax.swing.*;
import java.util.List;

public class WikipediaSeperationGUI {
    private JTextField txtFrom;
    private JTextField txtTo;
    private JList lstClusterWhitelist;
    private JList lstClusterBlacklist;
    private JButton btnToBlacklist;
    private JButton btnToWhiteList;
    private JButton btnCalculateShortestPath;
    private JPanel mainPanel;

    //region Initialisation
    private final ILinksFinderFacade facade;

    public WikipediaSeperationGUI(ILinksFinderFacade facade) {
        this.facade = facade;
        btnCalculateShortestPath.addActionListener(x -> calculateShortestPath());
        btnToBlacklist.addActionListener(x -> addSelectionToBlacklist());
        btnToWhiteList.addActionListener(x -> addSelectionToWhitelist());
    }
    //endregion

    //region Calculating shortest path
    private void calculateShortestPath() {
        String from = txtFrom.getText();
        String to = txtTo.getText();

        List<String> path = facade.calculateShortestPath(from, to);

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
