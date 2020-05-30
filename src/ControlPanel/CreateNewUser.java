package ControlPanel;

import Client.ClientConnector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateNewUser {

    protected JPanel createNewUserPanel;
    protected JButton backButton;
    protected JTextField user_idTextField;
    protected JTextField usernameTextField;
    protected JPasswordField passwordField;
    protected JPasswordField confirmPasswordField;
    protected JPanel credentialsPanel;
    protected JPanel titlePanel;
    protected JLabel createNewUserLabel;
    protected JLabel permissionsLabel;
    protected JLabel user_idLabel;
    protected JLabel usernameLabel;
    protected JLabel passwordLabel;
    protected JLabel confirmPasswordLabel;
    protected JCheckBox createBillboardsCheckBox;
    protected JCheckBox editAllBillboardsCheckBox;
    protected JCheckBox scheduleBillboardsCheckBox;
    protected JCheckBox editUsersCheckBox;
    private JButton saveUserButton;

    public CreateNewUser(JFrame frame, ClientConnector connector) {
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