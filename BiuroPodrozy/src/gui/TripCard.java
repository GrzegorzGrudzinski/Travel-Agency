package gui;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
// import java.util.List;

import i18n.Messages;
import model.Amenity;
import model.Trip;


/**
 * A card that displays the details of a trip in a compact format.
 */
public class TripCard extends JPanel {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public TripCard(Trip trip) {
        setLayout(new BorderLayout(10, 0));     // 10 px gap between columns
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),   // outer frame
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));    // inner padding

        add(createInfoPanel(trip), BorderLayout.CENTER);
        add(createPricePanel(trip), BorderLayout.EAST);

        // do not stretch vertically inside BoxLayout
        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
    }

    private JPanel createInfoPanel(Trip trip) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        panel.setOpaque(false);   // let the white card background show through

        JLabel name = new JLabel(trip.getName());
        name.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel dates = new JLabel(trip.getDestination() + " · " + Messages.format("card.dates",
                trip.getStartDate().format(DATE), trip.getEndDate().format(DATE), trip.getDays()));

        JLabel details = new JLabel(trip.getBoard() + " · " + trip.getTransport());

        String amenityText = trip.getAmenities().stream()
                .map(Amenity::toString)              // each enum -> translated name
                .collect(Collectors.joining(", "));  // "Basen, Plaża, Wi-Fi"
        JLabel amenities = new JLabel(amenityText);
        amenities.setForeground(Color.GRAY);

        panel.add(name);
        panel.add(Box.createVerticalStrut(4));
        panel.add(dates);
        panel.add(details);
        panel.add(amenities);
        return panel;
    }
    
    private JPanel createPricePanel(Trip trip) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel price = new JLabel(Messages.format("card.price", trip.getPrice()));
        price.setFont(new Font("SansSerif", Font.BOLD, 20));
        price.setForeground(new Color(0, 120, 180));
        price.setAlignmentX(Component.RIGHT_ALIGNMENT);

        int full = (int) Math.round(trip.getRating());
        JLabel rating = new JLabel("★".repeat(full) + "☆".repeat(5 - full) + " " + trip.getRating());
        rating.setForeground(new Color(230, 160, 0));
        rating.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel places = new JLabel(Messages.format("card.places", trip.getFreePlaces()));
        places.setAlignmentX(Component.RIGHT_ALIGNMENT);

        panel.add(price);
        panel.add(rating);
        panel.add(places);
        return panel;
    }
}