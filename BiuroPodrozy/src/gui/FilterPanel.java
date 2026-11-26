package gui;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import i18n.Messages;
import model.Board;
import model.Transport;
import model.Trip;
import filters.TripFilter;
import filters.TripSort;

/** Left-side panel with all search filters. */
public class FilterPanel extends JPanel {
    private final int maxPriceLimit;                  // slider maximum, rounded up
    private final JSlider priceSlider;
    private final JLabel priceLabel = new JLabel();
    private final JComboBox<String> destinationBox = new JComboBox<>();
    private final Map<Board, JCheckBox> boardChecks = new EnumMap<>(Board.class);
    private final ButtonGroup transportGroup = new ButtonGroup();
    private final JRadioButton anyTransport = new JRadioButton(Messages.get("filter.all"));
    private final Map<Transport, JRadioButton> transportRadios = new EnumMap<>(Transport.class);
    private final JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 5.0, 0.5));
    private final JComboBox<TripSort> sortBox = new JComboBox<>(TripSort.values());

    public FilterPanel(List<Trip> trips, Runnable onApply) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // price: slider max = highest price rounded up to full thousands
        double max = trips.stream().mapToDouble(Trip::getPrice).max().orElse(10000);
        maxPriceLimit = (int) Math.ceil(max / 1000) * 1000;
        priceSlider = new JSlider(0, maxPriceLimit, maxPriceLimit);
        priceSlider.addChangeListener(e -> updatePriceLabel());   // label follows the slider
        updatePriceLabel();
        addRow(priceLabel);
        addRow(priceSlider);

        // destination: "All" + every country from the data, without duplicates
        addHeader("filter.destination");
        destinationBox.addItem(Messages.get("filter.all"));
        trips.stream()
                .map(Trip::getDestination)
                .distinct()
                .sorted()
                .forEach(destinationBox::addItem);
        addRow(destinationBox);

        // board: one checkbox per enum value
        addHeader("filter.board");
        for (Board board : Board.values()) {
            JCheckBox check = new JCheckBox(board.toString());
            boardChecks.put(board, check);
            addRow(check);
        }

        // transport: "All" + one radio button per enum value
        addHeader("filter.transport");
        transportGroup.add(anyTransport);
        anyTransport.setSelected(true);
        addRow(anyTransport);
        for (Transport transport : Transport.values()) {
            JRadioButton radio = new JRadioButton(transport.toString());
            transportGroup.add(radio);
            transportRadios.put(transport, radio);
            addRow(radio);
        }

        addHeader("filter.minRating");
        addRow(ratingSpinner);

        addHeader("filter.sort");
        addRow(sortBox);

        // buttons
        JButton applyButton = new JButton(Messages.get("filter.apply"));
        applyButton.addActionListener(e -> onApply.run());
        JButton resetButton = new JButton(Messages.get("filter.reset"));
        resetButton.addActionListener(e -> {
            reset();
            onApply.run();
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.add(applyButton);
        buttons.add(resetButton);
        add(Box.createVerticalStrut(10));
        addRow(buttons);
    }

    // bold section title with some space above it
    private void addHeader(String key) {
        add(Box.createVerticalStrut(10));
        JLabel label = new JLabel(Messages.get(key));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        addRow(label);
    }

    // left-aligned row; stops BoxLayout from stretching combos/spinners vertically
    private void addRow(JComponent c) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        add(c);
    }

    /**Updates the price label based on the current slider value.*/
    private void updatePriceLabel() {
        priceLabel.setText(Messages.format("filter.maxPrice", priceSlider.getValue()));
    }



     /** Reads all controls and builds a filter; text comes from the search field. */
    public TripFilter buildFilter(String text) {
        // index 0 is "All" -> no restriction
        String destination = destinationBox.getSelectedIndex() == 0
                ? null
                : (String) destinationBox.getSelectedItem();

        Set<Board> boards = EnumSet.noneOf(Board.class);   // empty set of Board
        for (Map.Entry<Board, JCheckBox> entry : boardChecks.entrySet()) {
            if (entry.getValue().isSelected()) {
                boards.add(entry.getKey());
            }
        }

        Transport transport = null;                         // stays null if "All" is selected
        for (Map.Entry<Transport, JRadioButton> entry : transportRadios.entrySet()) {
            if (entry.getValue().isSelected()) {
                transport = entry.getKey();
            }
        }

        double minRating = ((Number) ratingSpinner.getValue()).doubleValue();
        return new TripFilter(text, priceSlider.getValue(), destination, boards, transport, minRating);
    }

    public TripSort getSortOrder() {
        return (TripSort) sortBox.getSelectedItem();
    }

    /** Restores default values of all controls. */
    public void reset() {
        priceSlider.setValue(maxPriceLimit);
        destinationBox.setSelectedIndex(0);
        for (JCheckBox check : boardChecks.values()) {
            check.setSelected(false);
        }
        anyTransport.setSelected(true);
        ratingSpinner.setValue(0.0);
        sortBox.setSelectedIndex(0);
    }
}