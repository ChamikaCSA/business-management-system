package app;

import views.AppGUI;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppGUI::new);
    }
}