package ControlPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login extends ControlPanel {
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
                try{
                    System.out.println(generateHash(passwordField.getPassword()));
                    Login.super.setContentPane(new OptionMenu().optionMenuPanel);
                    Login.super.pack();
                    Login.super.setLocationRelativeTo(null);
                    Login.super.setVisible(true);
                }
                catch(Exception ex) {
                    System.out.println(ex);
                }
            }
        });
    }

    public static String generateHash(char[] input) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final byte[] hashedBytes = digest.digest(
                new String(input).getBytes(StandardCharsets.UTF_8));
        String hex = bytesToHex(hashedBytes);

        return hex;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
}