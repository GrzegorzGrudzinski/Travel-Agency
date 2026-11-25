package filters;

import java.util.Comparator;

import i18n.Messages;
import model.Trip;

/** Available sort orders for search results. */
public enum TripSort {
    PRICE_ASC(Comparator.comparingDouble(Trip::getPrice)),
    PRICE_DESC(Comparator.comparingDouble(Trip::getPrice).reversed()),
    RATING_ASC(Comparator.comparingDouble(Trip::getRating)),
    RATING_DESC(Comparator.comparingDouble(Trip::getRating).reversed());

    private final Comparator<Trip> comparator;

    TripSort(Comparator<Trip> comparator) {   // enum constructors are always private
        this.comparator = comparator;
    }

    public Comparator<Trip> getComparator() {
        return comparator;
    }

    @Override
    public String toString() {
        return Messages.get("sort." + name());
    }
}