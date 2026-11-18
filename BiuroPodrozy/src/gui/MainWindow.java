package gui;

import javax.swing.*;
// import java.awt.*;


public class MainWindow extends JWindow {
    public static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(false);
        JFrame frame = new JFrame("Hello World");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final int sizeX = 1920/2;
        final int sizeY = 1080/2;
        frame.setSize(sizeX, sizeY);
        frame.setVisible(true); 
    }
}
