import javax.swing.SwingUtilities;

import gui.MainFrame;


public class TravelAgency {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

}
 

/*
# TODO:
- dodac resizowanie okna i komponentow
- przy wyjściu i wejsciu do wyszukiwania reset filtrow i wyszukiwania
- dodać wybór języka

*/