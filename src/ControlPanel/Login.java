package ControlPanel;

import Client.ClientConnector;
import SocketCommunication.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;

import static SocketCommunication.ServerRequest.LOGIN;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;

/**
 * A class to represent a "Login" page which is bound to Login.form
 * @author Joshua Young
 */
public class Login {
    protected JPanel loginPanel;
    protected JTextField usernameField;
    protected JPasswordField passwordField;
    protected JButton loginButton;
    protected JLabel loginLabel;
    protected JPanel usernameFieldPanel;
    protected JPanel passwordFieldPanel;
    protected JPanel loginButtonPanel;
    protected JPanel titlePanel;


    /**
     * Authenticates the user and opens an "Option Menu" page on successful login
     * @param frame the main frame in which the next page is to be placed
     */
    public Login(JFrame frame, ClientConnector connector) {
        loginButton.addActionListener(new ActionListener() {
            /**
             * Invoked when the "Login" button is clicked. Sends the entered username to the server to verify its
             * validity, hashes the entered password and sends it to the server to be verified, opens the "Option Menu"
             * page on successful verification or else notifies the user of the invalidity of the entered credentials
             * @param e the event of the "Login" button being pressed
             */
            @Override
            public void actionPerformed(ActionEvent e) {

                String username = usernameField.getText();
                char[] password = passwordField.getPassword();

                //get login data
                Credentials loginDetails = new Credentials(username, Arrays.toString(password));

                //create request
                Request loginRequest = new Request(LOGIN, loginDetails, null);

                // Send request to server
                Response response = null;

                try {
                    response = loginRequest.Send(connector);
                } catch (IOException excep) {
                    JOptionPane.showMessageDialog(null, "Cannot connect to server");
                    usernameField.setText("");
                    passwordField.setText("");
                    usernameField.requestFocus();
//                    return false;

                }

                // check status of response
                assert response != null;
                boolean status = response.isStatus();

                if (!status) {
                    String errorMsg = (String) response.getData();
                    JOptionPane.showMessageDialog(null, errorMsg);
                    usernameField.setText("");
                    passwordField.setText("");
                    usernameField.requestFocus();
//                    return false;
                    // return some error response if status is false
                } else {
                    // if status == true, get session object Session session = response.getData()
                    Session session = (Session) response.getData();

                    // Save session object and move onto next screen

                    try{
                        frame.setContentPane(new OptionMenu(frame, connector).optionMenuPanel);
                        frame.pack();
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);
                    }
                    catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }


            }
        });


    }


//    public static String generateHash(char[] input) throws NoSuchAlgorithmException {
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        byte[] hashedBytes = digest.digest(
//                new String(input).getBytes(StandardCharsets.UTF_8));
//        String hex = bytesToHex(hashedBytes);
//
//        return hex;
//    }

//    private static String bytesToHex(byte[] hash) {
//        StringBuffer hexString = new StringBuffer();
//
//        for (int i = 0; i < hash.length; i++) {
//            String hex = Integer.toHexString(0xff & hash[i]);
//            if(hex.length() == 1) hexString.append('0');
//            hexString.append(hex);
//        }
//
//        return hexString.toString();
//    }
}