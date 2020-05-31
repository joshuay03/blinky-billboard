package ControlPanel;

import Client.ClientConnector;
import SocketCommunication.Credentials;
import SocketCommunication.Request;
import SocketCommunication.Response;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EditExistingUser {
    protected JPanel editExistingUserPanel;
    protected JButton backButton;
    protected JPasswordField passwordField;
    protected JPasswordField confirmPasswordField;
    protected JPanel credentialsPanel;
    protected JPanel titlePanel;
    protected JLabel editExistingUserLabel;
    protected JLabel permissionsLabel;
    protected JLabel usernameLabel;
    protected JLabel passwordLabel;
    protected JLabel confirmPasswordLabel;
    protected JCheckBox createBillboardsCheckBox;
    protected JCheckBox editAllBillboardsCheckBox;
    protected JCheckBox scheduleBillboardsCheckBox;
    protected JCheckBox editUsersCheckBox;
    private JButton saveUserButton;
    private JComboBox<String> usernameCombo;

    public EditExistingUser(JFrame frame, ClientConnector connector) {
        Request userRequest = Request.listUsersReq(connector.session);

        // Send request to server
        Response response;
        // use global input stream, this is just to show how it works

        try {
            response = userRequest.Send(connector);
        } catch (IOException excep) {
            JOptionPane.showMessageDialog(null, "Cannot connect to server");
            return;
        }

        // check status of response
        boolean status = response.isStatus();

        if (!status) {
            String errorMsg = (String) response.getData();
            JOptionPane.showMessageDialog(null, errorMsg);
            return;
        }


        java.util.List<?> userObjects = (List<?>) response.getData();

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

        userObjects.forEach(x ->
                model.addElement((String) x)
        );

        usernameCombo.setModel(model);


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
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Arrays.equals(confirmPasswordField.getPassword(), passwordField.getPassword())) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match. Try again");
                    return;
                }

                String username = (String) usernameCombo.getSelectedItem();
                String password = new String(passwordField.getPassword());

                Credentials newUserCredentials = new Credentials(username, password);

                //Create request
                Request editUserReq = Request.editUserReq(newUserCredentials,
                        createBillboardsCheckBox.isSelected(), scheduleBillboardsCheckBox.isSelected(),
                        editAllBillboardsCheckBox.isSelected(), editUsersCheckBox.isSelected(), connector.session);

                // Send request to server
                Response response;

                try {
                    response = editUserReq.Send(connector);
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
                    JOptionPane.showMessageDialog(null, "User successfully updated.");
                }
            }
        });
    }

}
