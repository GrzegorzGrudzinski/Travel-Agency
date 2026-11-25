package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a trip offered by the travel agency.
 */
public class Trip {
    private final String name;             // hotel / trip name, matched by the search field
    private final String destination;      // destination country
    private final double price;            // price per person in PLN
    private final LocalDate startDate;     // first day of the trip
    private final int days;                // length of stay
    private final List<Amenity> amenities; // hotel amenities
    private final double rating;           // rating 0-5
    private final Transport transport;     // how to get there
    private final Board board;             // meal plan
    private int freePlaces;                // number of free places

    public Trip(String name, String destination, double price, LocalDate startDate, int days, List<Amenity> amenities, double rating, Transport transport, Board board, int freePlaces) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative: " + price);
        }
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5: " + rating);
        }
        if (days <= 0) {
            throw new IllegalArgumentException("Number of days must be positive: " + days);
        }
        if (freePlaces < 0) {
            throw new IllegalArgumentException("Number of free places cannot be negative: " + freePlaces);
        }
        
        this.name = name;
        this.destination = destination;
        this.price = price;
        this.startDate = startDate;
        this.days = days;
        this.amenities = List.copyOf(amenities);
        this.rating = rating;
        this.transport = transport;
        this.board = board;
        this.freePlaces = freePlaces;
    }
    public String getName() {
        return name;
    }
    public String getDestination() {
        return destination;
    }
    public double getPrice() {
        return price;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public int getDays() {
        return days;
    }
    public List<Amenity> getAmenities() {
        return amenities;
    }
    public double getRating() {
        return rating;
    }
    public Transport getTransport() {
        return transport;
    }
    public Board getBoard() {
        return board;
    }
    public int getFreePlaces() {
        return freePlaces;
    }


    public LocalDate getEndDate() {
        return startDate.plusDays(days);
    }

    @Override 
    public String toString() {
        return String.format("%s (%s), %s, %d day(s), %.2f zł, %s, %s, rating %.1f, %d free places", 
                name, destination, startDate, days, price, board, transport, rating, freePlaces);
    }


    // sample trips for testing and demonstration
    public static List<Trip> sampleTrips() {
        return List.of(
            new Trip("Hotel Aurora", "Grecja", 3499, LocalDate.of(2026, 7, 12), 7,
                    List.of(Amenity.POOL, Amenity.BEACH, Amenity.WIFI), 4.5,
                    Transport.PLANE, Board.ALL_INCLUSIVE, 20),
            new Trip("Santorini Blue Resort", "Grecja", 5899, LocalDate.of(2026, 8, 2), 10,
                    List.of(Amenity.POOL, Amenity.SPA, Amenity.WIFI), 4.8,
                    Transport.PLANE, Board.HALF_BOARD, 8),
            new Trip("Zakopane Chill", "Polska", 899, LocalDate.of(2027, 2, 1), 4,
                    List.of(Amenity.SAUNA, Amenity.WIFI, Amenity.PARKING), 4.0,
                    Transport.OWN, Board.BREAKFAST, 15),
            new Trip("Mazury Camp", "Polska", 650, LocalDate.of(2027, 7, 20), 5,
                    List.of(Amenity.BEACH, Amenity.PARKING), 3.2,
                    Transport.OWN, Board.NONE, 30),
            new Trip("Costa Brava Sun", "Hiszpania", 2799, LocalDate.of(2027, 6, 15), 7,
                    List.of(Amenity.POOL, Amenity.BEACH), 3.9,
                    Transport.BUS, Board.FULL_BOARD, 25),
            new Trip("Tenerife Resort", "Hiszpania", 4299, LocalDate.of(2026, 12, 10), 8,
                    List.of(Amenity.POOL, Amenity.ENTERTAINMENT, Amenity.WIFI), 4.3,
                    Transport.PLANE, Board.ALL_INCLUSIVE, 12),
            new Trip("Rzym Klasycznie", "Włochy", 2199, LocalDate.of(2027, 4, 5), 5,
                    List.of(Amenity.WIFI), 4.6,
                    Transport.TRAIN, Board.BREAKFAST, 18),
            new Trip("Dolomity Ski", "Włochy", 3899, LocalDate.of(2027, 1, 18), 7,
                    List.of(Amenity.SPA, Amenity.SAUNA, Amenity.PARKING), 4.1,
                    Transport.BUS, Board.HALF_BOARD, 10),
            new Trip("Hurghada Palace", "Egipt", 2499, LocalDate.of(2026, 12, 3), 7,
                    List.of(Amenity.POOL, Amenity.BEACH, Amenity.ENTERTAINMENT), 2.9,
                    Transport.PLANE, Board.ALL_INCLUSIVE, 22),
            new Trip("Paryż Weekend", "Francja", 1599, LocalDate.of(2027, 5, 1), 3,
                    List.of(Amenity.WIFI), 3.7,
                    Transport.TRAIN, Board.NONE, 6),
            new Trip("Malediwy Lux Resort", "Malediwy", 12999, LocalDate.of(2027, 2, 14), 12,
                    List.of(Amenity.POOL, Amenity.BEACH, Amenity.SPA, Amenity.WIFI), 5.0,
                    Transport.PLANE, Board.FULL_BOARD, 2)
        );
    }  
}
