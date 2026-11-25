package gui;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import java.awt.*;
import java.util.List;

import i18n.Messages;
import model.Trip;
import filters.*;

/**
 * Main search panel with a text field, filters, and a list of results.
 */
public class SearchPanel extends JPanel {
    private final List<Trip> allTrips;    // all trips, never changes
    private final JTextField searchField = new JTextField(20);
    private final JLabel resultCount = new JLabel();
    private final JPanel resultsPanel = new JPanel();    // list of TripCards
    private final FilterPanel filterPanel;

    // Creates a new search panel with the given trips and a callback for the back button.
    public SearchPanel(List<Trip> trips, Runnable onBack) {
        this.allTrips = trips;
        setLayout(new BorderLayout());
        
        // top bar: back button, search field, result count
        add(createTopBar(onBack), BorderLayout.NORTH);

        // results panel: vertical list of cards inside a scroll pane
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Theme.PAGE_BG);
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));   // space around cards

        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setBorder(null);                                // no extra frame around results
        scroll.getVerticalScrollBar().setUnitIncrement(16);   // faster mouse wheel scrolling
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        // create the filter panel on the left
        filterPanel = new FilterPanel(trips, this::refreshResults);
        JScrollPane filterScroll = new JScrollPane(filterPanel);
        filterScroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.CARD_BORDER));
        filterScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        filterScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(filterScroll, BorderLayout.WEST);

        // update results whenever the search text changes
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { refreshResults(); }  // text typed
            @Override public void removeUpdate(DocumentEvent e)  { refreshResults(); }  // text deleted
            @Override public void changedUpdate(DocumentEvent e) { refreshResults(); }  // style change, unused
        });

        refreshResults();   // show all trips at start
    }

    /** Creates the top bar with a back button, search field, and result count.*/
    private JPanel createTopBar(Runnable onBack) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8)); // left-aligned, 10px h-gap, 8px v-gap

        // back button: clears search and filters, then goes back to welcome screen
        JButton backButton = new JButton(Messages.get("search.back"));
        backButton.addActionListener(e -> {
            resetSearch(); // clear filters and search text before going back
            onBack.run();
        });

        bar.add(backButton);
        bar.add(new JLabel(Messages.get("search.label")));
        bar.add(searchField);
        bar.add(resultCount);
        return bar;
    }

    /** Updates the results panel to show only trips that match the current search text and filters.*/
    private void refreshResults() {
        TripFilter filter = filterPanel.buildFilter(searchField.getText());
        List<Trip> results = allTrips.stream()
                .filter(filter::matches)                                  // keep matching trips
                .sorted(filterPanel.getSortOrder().getComparator())       // apply chosen order
                .toList();

        resultsPanel.removeAll();
        for (Trip trip : results) {
            resultsPanel.add(new TripCard(trip));
            resultsPanel.add(Box.createVerticalStrut(8));
        }
        if (results.isEmpty()) {
            resultsPanel.add(new JLabel(Messages.get("search.noResults")));
        }
        resultCount.setText(Messages.format("search.found", results.size()));

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    /** Clears the text field and all filters, then shows every trip again. */
    private void resetSearch() {
        filterPanel.reset();
        searchField.setText("");
        refreshResults();
    }
}