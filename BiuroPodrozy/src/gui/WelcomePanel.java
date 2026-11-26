package gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import i18n.Messages;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Welcome screen with a background image, a language switch and a button that opens the search panel.
 */
public class WelcomePanel extends JPanel {
    private final BufferedImage background;   // null if the image could not be loaded

    public WelcomePanel(Runnable onStart, Consumer<Locale> onLanguageChange) {
        background = loadImage("/img/welcome.jpg");

        setLayout(new BorderLayout());
        add(createLanguageBar(onLanguageChange), BorderLayout.NORTH);   // PL / EN in the top-right corner

        JPanel center = new JPanel(new GridBagLayout());   // centres the content both ways
        center.setOpaque(false);                           // keep the background image visible

        JPanel content = new JPanel();
        content.setOpaque(false);         // transparent, so the background image shows through
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(Messages.get("welcome.title"));
        title.setFont(Theme.HERO);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(Messages.get("welcome.subtitle"));
        subtitle.setFont(Theme.SUBTITLE);
        subtitle.setForeground(Color.WHITE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = new JButton(Messages.get("welcome.searchButton"));
        startButton.setFont(Theme.BIG_BUTTON);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> onStart.run());

        content.add(title);
        content.add(Box.createVerticalStrut(10));   // 10 px gap
        content.add(subtitle);
        content.add(Box.createVerticalStrut(30));
        content.add(startButton);

        center.add(content);
        add(center, BorderLayout.CENTER);
    }

    /** One button per supported language; the current language is disabled. */
    private JPanel createLanguageBar(Consumer<Locale> onLanguageChange) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        bar.setOpaque(false);
        for (Locale lang : Messages.SUPPORTED) {
            JButton button = new JButton(lang.getLanguage().toUpperCase());   // "pl" -> "PL"
            button.setEnabled(!lang.equals(Messages.getLocale()));
            button.addActionListener(e -> onLanguageChange.accept(lang));
            bar.add(button);
        }
        return bar;
    }

    // Loads an image from the resources folder. Returns null if the image could not be loaded.
    private static BufferedImage loadImage(String path) {
        URL url = WelcomePanel.class.getResource(path);
        if (url == null) {
            System.err.println("Image not found: " + path);
            return null;
        }
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            System.err.println("Cannot read image: " + path + " (" + e.getMessage() + ")");
            return null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);   // clears the panel first
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            g.setColor(Theme.OVERLAY);        // darkens the image slightly
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // fallback when there is no image: plain colour background
            g.setColor(Theme.PRIMARY);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
