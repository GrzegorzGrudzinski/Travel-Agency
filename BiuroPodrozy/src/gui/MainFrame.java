package gui;

import javax.swing.*;

import data.TripRepository;
import i18n.Messages;
import model.Trip;

import java.awt.*;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;

/**
 * Main application window with a card layout that switches between the screen panels.
 */
public class MainFrame extends JFrame {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;

    private static final Path DATA_FILE = Path.of("data", "trips.txt");   // relative to working dir
    private final TripRepository repository = new TripRepository(DATA_FILE);
    
    // 
    private static final String SEARCH = "search";
    private static final String WELCOME = "welcome";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    
    public MainFrame() {
        setTitle(Messages.get("app.title"));
        cards.add(new WelcomePanel(() -> showCard(SEARCH)), WELCOME);
        
        List<Trip> trips = loadTrips();
        cards.add(new SearchPanel(trips, () -> showCard(WELCOME)), SEARCH);

        cardLayout.show(cards, WELCOME);
        
        setContentPane(cards);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(800, 500));   // below this the filter column and cards overlap
        setLocationRelativeTo(null);                // centre the window on screen
    }
    private void showCard(String name) {
        cardLayout.show(cards, name);
    }
    
    private List<Trip> loadTrips() {
        try {
            return repository.load();
        } catch (IOException e) {
            System.err.println("Cannot read " + DATA_FILE.toAbsolutePath() + ": " + e.getMessage());
            return Trip.sampleTrips();   // fallback data so the app still starts
        }
    }
}
