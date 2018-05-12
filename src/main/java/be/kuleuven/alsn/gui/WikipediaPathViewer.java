package be.kuleuven.alsn.gui;

import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPath;
import be.kuleuven.alsn.facade.IWikipediaCommunityFacade;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

public class WikipediaPathViewer {
    private final IWikipediaCommunityFacade communityFacade;
    private final WikiPath path;
    private JList<WikiPageCard> lstPath;
    private JPanel mainPanel;
    private JButton viewCommunitiesButton;
    private JButton blockPageCommunityButton;
    private JButton recalculateButton;

    public WikipediaPathViewer(IWikipediaCommunityFacade communityFacade, WikiPath path, Runnable recalculatePath) {
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
        viewCommunitiesButton.addActionListener(x -> viewCommunitySelectedNode());
        recalculateButton.addActionListener(x -> {
            recalculateButton.setEnabled(false);
            SwingUtilities.invokeLater(() -> {
                recalculatePath.run();
                recalculateButton.setEnabled(true);
            });
        });
        blockPageCommunityButton.addActionListener(x -> toggleBlockStatus());
        communityFacade.addBlockListener(x -> updateSelected());
        communityFacade.addUnblockListener(x -> updateSelected());
        lstPath.addListSelectionListener(this::updateSelected);
    }

    private void viewCommunitySelectedNode() {
        lstPath.getSelectedValuesList().forEach(page -> {
            WikiCommunityToken community = communityFacade.getCommunityOf(page);
            SwingUtilities.invokeLater(() -> new CommunityViewer(communityFacade, community));
        });
    }

    private void toggleBlockStatus() {
        WikiPageCard page = lstPath.getSelectedValue();
        WikiCommunityToken community = communityFacade.getCommunityOf(page);
        if (communityFacade.isBlocked(community)) {
            communityFacade.unblockCommunity(community);
        } else {
            communityFacade.blockCommunity(community);
        }

    }

    private void updateSelected() {
        blockPageCommunityButton.setEnabled(!lstPath.isSelectionEmpty());
        if (communityFacade.isBlocked(communityFacade.getCommunityOf(lstPath.getSelectedValue()))) {
            blockPageCommunityButton.setText("Unblock Page Community");
        } else {
            blockPageCommunityButton.setText("Block Page Community");
        }
    }

    private void updateSelected(ListSelectionEvent listSelectionEvent) {
        updateSelected();
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
