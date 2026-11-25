package data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import model.Amenity;
import model.Board;
import model.Transport;
import model.Trip;

/** Reads and writes trips from/to a text file, one trip per line. */
public class TripRepository {
    private static final String SEPARATOR = ";";
    private static final String LIST_SEPARATOR = ",";
    private static final int FIELD_COUNT = 10;
    private static final String HEADER =
            "# name;destination;price;startDate;days;amenities;rating;transport;board;freePlaces";

    private final Path file;   // where the data lives

    public TripRepository(Path file) {
        this.file = file;
    }

    /** Reads all trips; skips empty lines and lines starting with '#'. */
    public List<Trip> load() throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<Trip> trips = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {      // index loop: line numbers needed in stage 7
            String line = lines.get(i).strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;                              // blank line or comment
            }
            trips.add(parseLine(line));
        }
        return trips;
    }

    /** Parses a single line into a Trip object.*/
    private static Trip parseLine(String line) {
        String[] f = line.split(SEPARATOR, -1);   // -1: keep empty fields (e.g. no amenities)
        if (f.length != FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + FIELD_COUNT + " fields, got " + f.length);
        }
        for (int i = 0; i < f.length; i++) {
            f[i] = f[i].strip();                  // tolerate spaces around ';'
        }
        return new Trip(
                f[0],                             // name
                f[1],                             // destination
                Double.parseDouble(f[2]),         // price
                LocalDate.parse(f[3]),            // start date, ISO yyyy-MM-dd
                Integer.parseInt(f[4]),           // days
                parseAmenities(f[5]),
                Double.parseDouble(f[6]),         // rating
                Transport.valueOf(f[7]),
                Board.valueOf(f[8]),
                Integer.parseInt(f[9]));          // free places
    }

    /** Parses a list of amenities. */
    private static List<Amenity> parseAmenities(String text) {
        if (text.isEmpty()) {
            return List.of();                     // "" would otherwise become Amenity.valueOf("")
        }
        List<Amenity> amenities = new ArrayList<>();
        for (String part : text.split(LIST_SEPARATOR)) {
            amenities.add(Amenity.valueOf(part.strip()));
        }
        return amenities;
    }

    /** Overwrites the file with the given trips (used after reservations in stage 8). */
    public void save(List<Trip> trips) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Trip trip : trips) {
            lines.add(toLine(trip));
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    /** Converts a trip to a single line of text. */
    private static String toLine(Trip t) {
        String amenities = t.getAmenities().stream()
                .map(Amenity::name)                   // name(), NOT toString()
                .collect(Collectors.joining(LIST_SEPARATOR));
        return String.join(SEPARATOR,
                t.getName(), t.getDestination(), String.valueOf(t.getPrice()),
                t.getStartDate().toString(), String.valueOf(t.getDays()), amenities,
                String.valueOf(t.getRating()), t.getTransport().name(), t.getBoard().name(),
                String.valueOf(t.getFreePlaces()));
    }
}