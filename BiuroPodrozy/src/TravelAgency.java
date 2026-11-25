import java.time.LocalDate;
import java.util.List;

import javax.swing.SwingUtilities;

import gui.MainFrame;
import model.Board;
import model.Transport;
import model.Trip;


public class TravelAgency {
    public static void main(String[] args) {
        Trip.sampleTrips().forEach(System.out::println);
        try {
            new Trip("Test", "X", -100, LocalDate.of(2026, 1, 1), 3,
                    List.of(), 4.0, Transport.BUS, Board.NONE);
        } 
        catch (IllegalArgumentException e) {
            System.out.println("Walidacja działa: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

}
 