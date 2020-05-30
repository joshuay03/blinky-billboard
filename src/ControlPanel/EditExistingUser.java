package ControlPanel;

import Client.ClientConnector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditExistingUser {
    protected JPanel editExistingUserPanel;
    protected JButton backButton;
    protected JTextField user_idTextField;
    protected JTextField usernameTextField;
    protected JPasswordField passwordField;
    protected JPasswordField confirmPasswordField;
    protected JPanel credentialsPanel;
    protected JPanel titlePanel;
    protected JLabel editExistingUserLabel;
    protected JLabel permissionsLabel;
    protected JLabel user_idLabel;
    protected JLabel usernameLabel;
    protected JLabel passwordLabel;
    protected JLabel confirmPasswordLabel;
    protected JCheckBox createBillboardsCheckBox;
    protected JCheckBox editAllBillboardsCheckBox;
    protected JCheckBox scheduleBillboardsCheckBox;
    protected JCheckBox editUsersCheckBox;

    public EditExistingUser(JFrame frame, ClientConnector connector) {
        backButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new EditUsers(frame, connector).editUsersPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
