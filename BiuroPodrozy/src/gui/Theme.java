package gui;

import java.awt.*;

/** Shared colours and fonts used by all screens. */
public final class Theme {
    private Theme() {}   // constants only, no instances

    public static final Color PRIMARY = new Color(0, 120, 180);      // prices, accents
    public static final Color ACCENT = new Color(230, 160, 0);       // rating stars
    public static final Color WARNING = new Color(200, 40, 40);      // few places left
    public static final Color PAGE_BG = new Color(236, 240, 244);    // behind the cards
    public static final Color CARD_BG = Color.WHITE;
    public static final Color CARD_BORDER = new Color(215, 220, 225);
    public static final Color TEXT_MUTED = Color.GRAY;
    public static final Color OVERLAY = new Color(0, 0, 0, 90);      // darkens the welcome image

    private static final String FAMILY = "SansSerif";
    public static final Font HERO = new Font(FAMILY, Font.BOLD, 48);
    public static final Font SUBTITLE = new Font(FAMILY, Font.PLAIN, 20);
    public static final Font BIG_BUTTON = new Font(FAMILY, Font.BOLD, 20);
    public static final Font CARD_TITLE = new Font(FAMILY, Font.BOLD, 18);
    public static final Font PRICE = new Font(FAMILY, Font.BOLD, 20);
}