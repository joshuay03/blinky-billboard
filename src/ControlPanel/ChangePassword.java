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

public class ChangePassword {
    protected JPanel changePasswordPanel;
    protected JButton backButton;
    protected JPasswordField passwordField;
    protected JPasswordField confirmPasswordField;
    protected JPanel credentialsPanel;
    protected JPanel titlePanel;
    protected JLabel changePasswordLabel;
    protected JLabel passwordLabel;
    protected JLabel confirmPasswordLabel;
    private JButton saveButton;

    public ChangePassword(JFrame frame, ClientConnector connector) {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Arrays.equals(confirmPasswordField.getPassword(), passwordField.getPassword())) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match. Try again");
                    return;
                }

                String password = new String(passwordField.getPassword());

                Credentials newUserCredentials = new Credentials(connector.session.username, password);

                //Create request
                Request editUserReq = Request.editUserReq(newUserCredentials,
                        connector.session.canCreateBillboards, connector.session.scheduleBillboards,
                        connector.session.editAllBillboards, connector.session.editUsers, connector.session);

                // Send request to server
                Response response;

                try {
                    response = editUserReq.Send(connector);
                } catch (IOException excep) {
                    JOptionPane.showMessageDialog(null, "Cannot change password.");
                    return;
                }

                // check status of response
                boolean status = response.isStatus();

                if (!status) {
                    String errorMsg = (String) response.getData();
                    JOptionPane.showMessageDialog(null, "Cannot change password. Error: " + errorMsg);
                }

                if (status) {
                    JOptionPane.showMessageDialog(null, "Password change successful. Login again using new password.");
                    frame.setContentPane(new Login(frame, connector).loginPanel);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            }
        });
    }
}
