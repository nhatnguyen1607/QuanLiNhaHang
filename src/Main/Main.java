package Main;

import javax.swing.SwingUtilities;

import ui.DangNhapUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DangNhapUI::new);
    }
}
