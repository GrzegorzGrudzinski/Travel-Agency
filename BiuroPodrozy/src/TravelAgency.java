import javax.swing.SwingUtilities;

import gui.MainFrame;
import model.Board;
import model.Transport;

public class TravelAgency {
    public static void main(String[] args) {
        for (Board b : Board.values()) {
            System.out.println(b.name() + " -> " + b);
        }
        for (Transport t : Transport.values()) {
            System.out.println(t.name() + " -> " + t);
        }
        System.out.println(Board.valueOf("ALL_INCLUSIVE") == Board.ALL_INCLUSIVE);
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

}
 