package gui;

import javax.swing.*;

import i18n.Messages;
import model.Trip;

import java.awt.*;

/**
 * Main application window with a card layout that switches between the screen panels.
 */
public class MainFrame extends JFrame {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    
    // 
    private static final String SEARCH = "search";
    private static final String WELCOME = "welcome";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    
    public MainFrame() {
        setTitle(Messages.get("app.title"));
        cards.add(new WelcomePanel(() -> showCard(SEARCH)), WELCOME);
        cards.add(new SearchPanel(Trip.sampleTrips(), () -> showCard(WELCOME)), SEARCH);
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
    
    /*
    private JPanel createCard(String title, String buttonText, String target) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        JButton button = new JButton(buttonText);
        button.addActionListener(e -> showCard(target));

        JPanel bottom = new JPanel();
        bottom.add(button);
        panel.add(bottom, BorderLayout.PAGE_END);

        return panel;
    }
    */
}
