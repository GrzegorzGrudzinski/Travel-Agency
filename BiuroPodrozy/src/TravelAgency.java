import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import gui.MainFrame;


public class TravelAgency {
     public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // useNimbus();
            new MainFrame().setVisible(true);
        });
    }

    private static void useNimbus() {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (Exception e) {
                    System.err.println("Cannot set Nimbus: " + e.getMessage());
                }
                return;
            }
        }
    }

}
 

/*
# TODO:
- dodac resizowanie okna i komponentow
- przy wyjściu i wejsciu do wyszukiwania reset filtrow i wyszukiwania
- dodać wybór języka

*/