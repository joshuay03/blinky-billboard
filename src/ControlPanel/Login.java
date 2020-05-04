package ControlPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {
    protected JPanel loginPanel;
    protected JTextField usernameField;
    protected JPasswordField passwordField;
    protected JButton loginButton;

    public Login() {
        loginButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Passed");
            }
        });
    }
}
