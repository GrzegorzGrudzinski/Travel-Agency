import javax.swing.SwingUtilities;

import gui.MainFrame;

public class TravelAgency {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

}
 