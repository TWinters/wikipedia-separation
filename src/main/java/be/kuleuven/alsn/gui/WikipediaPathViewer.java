package be.kuleuven.alsn.gui;

import be.kuleuven.alsn.data.WikipediaPageCard;
import be.kuleuven.alsn.data.WikipediaPath;

import javax.swing.*;

public class WikipediaPathViewer {
    private final WikipediaPath path;
    private JList<WikipediaPageCard> lstPath;
    private JPanel mainPanel;

    public WikipediaPathViewer(WikipediaPath path) {
        this.path = path;
        ListModel<WikipediaPageCard> listModel = new AbstractListModel<WikipediaPageCard>() {
            @Override
            public int getSize() {
                return path.getPathLength();
            }

            @Override
            public WikipediaPageCard getElementAt(int index) {
                return path.getPage(index);
            }
        };
        this.lstPath.setModel(listModel);
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
