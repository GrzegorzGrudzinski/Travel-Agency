package gui;

import javax.swing.*;

import data.TripParseException;
import data.TripRepository;
import i18n.Messages;
import model.Trip;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Main application window with a card layout that switches between the screen panels.
 */
public class MainFrame extends JFrame {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;

    private static final Path DATA_FILE = Path.of("data", "trips.txt");   // relative to working dir

    // names of the cards in the CardLayout
    private static final String SEARCH = "search";
    private static final String WELCOME = "welcome";

    private final TripRepository repository = new TripRepository(DATA_FILE);
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final List<Trip> trips;   // loaded once; reused when the UI is rebuilt

    public MainFrame() {
        trips = loadTrips();   // the file is read only once
        buildCards();

        setContentPane(cards);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(800, 500));   // below this the filter column and cards overlap
        setLocationRelativeTo(null);                // centre the window on screen
    }

    /** Creates all screens from scratch, so they pick up texts in the current language. */
    private void buildCards() {
        setTitle(Messages.get("app.title"));
        cards.removeAll();
        cards.add(new WelcomePanel(() -> showCard(SEARCH), this::changeLanguage), WELCOME);
        cards.add(new SearchPanel(trips, () -> showCard(WELCOME)), SEARCH);
        cardLayout.show(cards, WELCOME);
        cards.revalidate();
        cards.repaint();
    }

    private void changeLanguage(Locale locale) {
        Messages.setLocale(locale);
        buildCards();
    }

    private void showCard(String name) {
        cardLayout.show(cards, name);
    }

    /** Loads trips from the data file, shows a warning dialog for any skipped lines. */
    private List<Trip> loadTrips() {
        try {
            TripRepository.LoadResult result = repository.load();
            if (!result.errors().isEmpty()) {
                showSkippedLines(result.errors());
            }
            return result.trips();
        } catch (IOException e) {
            System.err.println("Cannot read " + DATA_FILE.toAbsolutePath() + ": " + e);
            JOptionPane.showMessageDialog(null,
                    Messages.format("error.fileRead", DATA_FILE.toAbsolutePath(), e.getClass().getSimpleName()),
                    Messages.get("error.title"),
                    JOptionPane.ERROR_MESSAGE);
            return Trip.sampleTrips();   // fallback so the app still starts
        }
    }

    /** Shows a warning dialog with the lines that were skipped due to errors. */
    private void showSkippedLines(List<TripParseException> errors) {
        StringBuilder text = new StringBuilder(Messages.format("error.skippedLines", errors.size()));
        for (TripParseException e : errors) {
            System.err.println("Skipped line " + e.getLineNumber() + ": " + e.getMessage());   // for the developer
            text.append('\n').append(Messages.format("error.line", e.getLineNumber(), e.getMessage()));
        }
        JOptionPane.showMessageDialog(null, text.toString(),
                Messages.get("warning.title"), JOptionPane.WARNING_MESSAGE);
    }
}