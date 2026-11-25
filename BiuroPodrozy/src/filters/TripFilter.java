package filters;

import java.util.Set;

import model.Board;
import model.Transport;
import model.Trip;

/** Search criteria; decides whether a trip matches them. */
public class TripFilter {
    private final String text;          // lower-case, trimmed
    private final double maxPrice;
    private final String destination;   // null = any
    private final Set<Board> boards;    // empty = any
    private final Transport transport;  // null = any
    private final double minRating;

    public TripFilter(String text, double maxPrice, String destination,
                      Set<Board> boards, Transport transport, double minRating) {
        this.text = text.trim().toLowerCase();
        this.maxPrice = maxPrice;
        this.destination = destination;
        this.boards = Set.copyOf(boards);
        this.transport = transport;
        this.minRating = minRating;
    }

    public boolean matches(Trip t) {
        if (!t.getName().toLowerCase().contains(text)) return false;
        if (t.getPrice() > maxPrice) return false;
        if (destination != null && !t.getDestination().equals(destination)) return false;
        if (!boards.isEmpty() && !boards.contains(t.getBoard())) return false;
        if (transport != null && t.getTransport() != transport) return false;
        return t.getRating() >= minRating;
    }
}