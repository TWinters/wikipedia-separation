package be.kuleuven.alsn.gui;

import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPath;
import be.kuleuven.alsn.facade.IWikipediaCommunityFacade;

import javax.swing.*;
import java.util.stream.Collectors;

public class WikipediaPathViewer {
    private final IWikipediaCommunityFacade communityFacade;
    private final WikiPath path;
    private JList<WikiPageCard> lstPath;
    private JPanel mainPanel;
    private JButton viewCommunitiesButton;

    public WikipediaPathViewer(IWikipediaCommunityFacade communityFacade, WikiPath path) {
        this.communityFacade = communityFacade;
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
        WikiPageCard page = lstPath.getSelectedValue();
        System.out.println(path.getPages().stream().map(WikiPageCard::getPageId)
                .map(e->e+"")
                .collect(Collectors.joining(",")));
        System.out.println("Viewing community of " + page + ":" +page.getPageId());
        WikiCommunityToken community = communityFacade.getCommunityOf(page);
        new CommunityViewer(communityFacade, community);
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
