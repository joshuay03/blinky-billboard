package ControlPanel;

import Client.ClientConnector;
import SocketCommunication.Credentials;
import SocketCommunication.Request;
import SocketCommunication.Response;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class CreateNewUser {

    protected JPanel createNewUserPanel;
    protected JButton backButton;
    protected JTextField usernameTextField;
    protected JPasswordField passwordField;
    protected JPasswordField confirmPasswordField;
    protected JPanel credentialsPanel;
    protected JPanel titlePanel;
    protected JLabel createNewUserLabel;
    protected JLabel permissionsLabel;
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

        saveUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (confirmPasswordField.getPassword() != passwordField.getPassword()) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match. Try again");
                    return;
                }

                String username = usernameTextField.getText();
                String password = new String(passwordField.getPassword());

                Credentials newUserCredentials = new Credentials(username, password);

                //Create request
                Request createNewUser = Request.createUserReq(newUserCredentials,
                        createBillboardsCheckBox.isSelected(), scheduleBillboardsCheckBox.isSelected(),
                        editAllBillboardsCheckBox.isSelected(), editUsersCheckBox.isSelected(), connector.session);

                // Send request to server
                Response response;

                try {
                    response = createNewUser.Send(connector);
                } catch (IOException excep) {
                    JOptionPane.showMessageDialog(null, "Cannot save user.");
                    return;
                }

                // check status of response
                boolean status = response.isStatus();

                if (!status) {
                    String errorMsg = (String) response.getData();
                    JOptionPane.showMessageDialog(null, "Cannot save user. Error: " + errorMsg);
                }

                if (status) {
                    JOptionPane.showMessageDialog(null, "User successfully created.");
                }
            }
        });
    }
}