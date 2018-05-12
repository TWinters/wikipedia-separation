package be.kuleuven.alsn.gui;

import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageWithLinksCount;
import be.kuleuven.alsn.facade.IWikipediaCommunityFacade;

import javax.swing.*;
import java.util.List;

public class CommunityViewer {
    private JFrame frame;
    private JList<WikiPageWithLinksCount> lstNodes;
    private JButton blockCommunityButton;
    private JPanel mainPanel;
    private JLabel lblCommunityId;
    private JButton unblockCommunityButton;

    private IWikipediaCommunityFacade communityFacade;
    private WikiCommunityToken community;

    public CommunityViewer(IWikipediaCommunityFacade communityFacade, WikiCommunityToken community) {
        this.communityFacade = communityFacade;
        this.community = community;

        // Initialise frame
        initialiseFrame();

        // Initialise label
        lblCommunityId.setText(Long.toString(community.getId()));

        // Initialise list with page nodes
        List<WikiPageWithLinksCount> communityPages = communityFacade.getCommunityPages(community);
        System.out.println("pages:" +communityPages);
        DefaultListModel<WikiPageWithLinksCount> lstNodesModel = new DefaultListModel<>();
        communityPages.forEach(lstNodesModel::addElement);
        lstNodes.setModel(lstNodesModel);

        // Initialise block buttons
        updateBlockedButtonEnabledness();
        blockCommunityButton.addActionListener(x -> communityFacade.blockCommunity(community));
        unblockCommunityButton.addActionListener(x -> communityFacade.unblockCommunity(community));
        communityFacade.addBlockListener(x->updateBlockedButtonEnabledness());
        communityFacade.addUnblockListener(x->updateBlockedButtonEnabledness());
    }

    public void updateBlockedButtonEnabledness() {
        boolean isBlocked = communityFacade.isBlocked(community);
        blockCommunityButton.setEnabled(!isBlocked);
        unblockCommunityButton.setEnabled(isBlocked);
    }

    private void initialiseFrame() {
        frame = new JFrame("Community Viewer");
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

}
