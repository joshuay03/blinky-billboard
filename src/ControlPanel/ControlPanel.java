package ControlPanel;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JFrame {
    private static ControlPanel controlPanel;

    public ControlPanel() {
        super("Control Panel");
        super.setPreferredSize(new Dimension(400, 300));
        super.setDefaultCloseOperation(super.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        controlPanel = new ControlPanel();
        Login panel = new Login(controlPanel);
        controlPanel.setContentPane(panel.loginPanel);
        controlPanel.pack();
        controlPanel.setVisible(true);
        controlPanel.setLocationRelativeTo(null);
    }

    public void changePanel(JPanel panel) {
        controlPanel.setContentPane(panel);
        controlPanel.pack();
        controlPanel.setVisible(true);
    }
}