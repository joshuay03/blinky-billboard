package ControlPanel;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JFrame implements Runnable {

    public ControlPanel() {
        super("Control Panel");
        super.setPreferredSize(new Dimension(400, 300));
        super.setDefaultCloseOperation(super.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        ControlPanel controlPanel = new ControlPanel();
        SwingUtilities.invokeLater(controlPanel);
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        super.setContentPane(new Login().loginPanel);
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true);
    }
}