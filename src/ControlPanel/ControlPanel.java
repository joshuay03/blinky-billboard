package ControlPanel;

import javax.swing.*;
import java.awt.*;

public class ControlPanel {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Control Panel");
        frame.setPreferredSize(new Dimension(400, 300));
        JPanel mainPanel = new Login().loginPanel;
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
