package data;

/** Thrown when one line of the trips file cannot be turned into a Trip. */
public class TripParseException extends Exception {
    private final int lineNumber;

    public TripParseException(int lineNumber, String message, Throwable cause) {
        super(message, cause);         // cause = the original exception (e.g. NumberFormatException)
        this.lineNumber = lineNumber;
    }

    /** Thrown when one line of the trips file cannot be turned into a Trip. */
    public int getLineNumber() {
        return lineNumber;
    }
}