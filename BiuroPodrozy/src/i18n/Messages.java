
package i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** Access to translated texts; the language can be changed at runtime. */
public final class Messages {
    private static final String BUNDLE_NAME = "i18n.messages";
    public static final List<Locale> SUPPORTED = List.of(Locale.of("pl"), Locale.of("en"));

    private static Locale locale = SUPPORTED.get(0);          // Polish by default
    private static ResourceBundle bundle = loadBundle(locale);

    private Messages() {}   // utility class, no instances

    private static ResourceBundle loadBundle(Locale locale) {
        try {
            // no fallback to the system language: "pl" must never load messages_en
            return ResourceBundle.getBundle(BUNDLE_NAME, locale,
                    ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT));
        } catch (MissingResourceException e) {
            System.err.println("Translation not found: " + e.getMessage());
            return null;
        }
    }

    public static Locale getLocale() {
        return locale;
    }

    /** Switches the language; panels created after this call use the new texts. */
    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        bundle = loadBundle(newLocale);
    }

    /** Returns the translated text, or "!key!" if the key or the whole file is missing. */
    public static String get(String key) {
        if (bundle == null) {
            return "!" + key + "!";
        }
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
    }

    /** Returns the translation with {0}, {1}... replaced; numbers formatted for the current language. */
    public static String format(String key, Object... args) {
        return new MessageFormat(get(key), locale).format(args);
    }
}
