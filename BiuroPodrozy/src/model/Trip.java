package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a trip offered by the travel agency.
 */
public class Trip {
    private final String name;             // 
    private final String destination;      // 
    private final double price;            // 
    private final LocalDate startDate;     // 
    private final int days;                // 
    private final List<Amenity> amenities; // 
    private final double rating;           // 
    private final Transport transport;     // 
    private final Board board;             // 

    public Trip(String name, String destination, double price, LocalDate startDate, int days, List<Amenity> amenities, double rating, Transport transport, Board board) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative: " + price);
        }
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5: " + rating);
        }
        if (days <= 0) {
            throw new IllegalArgumentException("Number of days must be positive: " + days);
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


    public LocalDate getEndDate() {
        return startDate.plusDays(days);
    }

    public String toString() {
    return String.format("%s (%s), %s, %d day(s), %.2f zł, %s, %s, rating %.1f",
                name, destination, startDate, days, price, board, transport, rating);
    }


    public static List<Trip> sampleTrips() {
        return List.of(
            new Trip("Sunny Beach Escape", "Sunny Beach", 1200.0, LocalDate.of(2024, 7, 15), 7, List.of(Amenity.POOL, Amenity.WIFI), 4.5, Transport.PLANE, Board.ALL_INCLUSIVE),
            new Trip("Mountain Adventure", "Alps", 900.0, LocalDate.of(2024, 8, 1), 10, List.of(Amenity.SPA, Amenity.SAUNA), 4.8, Transport.BUS, Board.HALF_BOARD)
        );
    }

   
}
