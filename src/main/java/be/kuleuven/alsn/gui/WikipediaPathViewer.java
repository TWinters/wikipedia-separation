package be.kuleuven.alsn.gui;

import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPath;

import javax.swing.*;

public class WikipediaPathViewer {
    private final WikiPath path;
    private JList<WikiPageCard> lstPath;
    private JPanel mainPanel;
    private JButton viewCommunitiesButton;

    public WikipediaPathViewer(WikiPath path) {
        this.path = path;
        ListModel<WikiPageCard> listModel = new AbstractListModel<WikiPageCard>() {
            @Override
            public int getSize() {
                return path.getPathLength();
            }

            @Override
            public WikiPageCard getElementAt(int index) {
                return path.getPage(index);
            }
        };
        this.lstPath.setModel(listModel);
        viewCommunitiesButton.addActionListener(x -> viewCommunitiesSelectedNode());
    }

    private void viewCommunitiesSelectedNode() {
        System.out.println(lstPath.getSelectedValue());
    }

    //region Running the GUI
    public void run() {
        JFrame frame = new JFrame("Wikipedia Path GUI");
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    //endregion
}
