package be.kuleuven.alsn.gui;

import be.kuleuven.alsn.data.WikiCommunityToken;
import be.kuleuven.alsn.data.WikiPageCard;
import be.kuleuven.alsn.data.WikiPath;
import be.kuleuven.alsn.facade.IWikipediaCommunityFacade;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.Optional;

public class WikipediaPathViewer {
    private final IWikipediaCommunityFacade communityFacade;
    private final WikiPath path;
    private JList<WikiPageCard> lstPath;
    private JPanel mainPanel;
    private JButton viewCommunitiesButton;
    private JButton blockPageCommunityButton;
    private JButton recalculateButton;
    private JButton unblockPageCommunityButton;

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
        blockPageCommunityButton.addActionListener(x -> this.block());
        unblockPageCommunityButton.addActionListener(x -> this.unblock());
        communityFacade.addBlockListener(x -> updateSelected());
        communityFacade.addUnblockListener(x -> updateSelected());
        lstPath.addListSelectionListener(this::updateSelected);
    }

    private Optional<WikiCommunityToken> getCommunityToken() {
        if (lstPath.isSelectionEmpty()) {
            return Optional.empty();
        }
        WikiPageCard page = lstPath.getSelectedValue();
        WikiCommunityToken community = communityFacade.getCommunityOf(page);
        return Optional.of(community);

    }

    private void unblock() {
        unblockPageCommunityButton.setEnabled(false);
        SwingUtilities.invokeLater(() -> {
            Optional<WikiCommunityToken> token = getCommunityToken();
            token.ifPresent(communityFacade::unblockCommunity);
        });
    }

    private void block() {
        blockPageCommunityButton.setEnabled(false);
        SwingUtilities.invokeLater(() -> {
            Optional<WikiCommunityToken> token = getCommunityToken();
            token.ifPresent(communityFacade::blockCommunity);
        });
    }

    private void viewCommunitySelectedNode() {
        lstPath.getSelectedValuesList().forEach(page -> {
            WikiCommunityToken community = communityFacade.getCommunityOf(page);
            SwingUtilities.invokeLater(() -> new CommunityViewer(communityFacade, community));
        });
    }


    private void updateSelected() {
        boolean isBlocked = communityFacade.isBlocked(communityFacade.getCommunityOf(lstPath.getSelectedValue()));
        blockPageCommunityButton.setEnabled(!lstPath.isSelectionEmpty() && !isBlocked);
        unblockPageCommunityButton.setEnabled(!lstPath.isSelectionEmpty() && isBlocked);
    }

    private void updateSelected(ListSelectionEvent listSelectionEvent) {
        updateSelected();
    }

    //region Running the GUI
    public void run() {
        JFrame frame = new JFrame("Path Viewer");
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    //endregion

}
