package i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Messages {
    // 1. load translation files
    private static final ResourceBundle BUNDLE = loadBundle();
    
    private static ResourceBundle loadBundle() {
        try {
            return ResourceBundle.getBundle("i18n.messages", Locale.of("pl"));
        } 
        catch (MissingResourceException e) {
            System.err.println("Translation not found: " + e.getMessage());
            return null;
        }
    }

    // 2. get the translation
    private Messages() {} //

    public static String get(String key) {
        if (BUNDLE == null) {
           return "!" + key + "!";
        }
        try {
            return BUNDLE.getString(key);
        } 
        catch (MissingResourceException e) {
            return "!" + key + "!"; // missing value
        }
    }

    /** Returns the translation with {0}, {1}... replaced by the given values. */
    public static String format(String key, Object... args) {
        return MessageFormat.format(get(key), args);
    }
}
