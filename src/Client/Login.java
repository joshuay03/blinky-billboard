package Client;

import SocketCommunication.Request;
import SocketCommunication.Response;
import SocketCommunication.Session;

import static SocketCommunication.ServerRequest.*;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;


public class Login extends JFrame {

    JPanel panel = new JPanel();
    JTextField userField = new JTextField(15);
    JPasswordField passField = new JPasswordField(15);
    JButton loginButton = new JButton("Login");

    public Login() {
        super("Login");
        setSize(300, 200);
        setLocation(500, 280);
        panel.setLayout(null);

        userField.setBounds(70, 30, 150, 20);
        passField.setBounds(70, 65, 150, 20);
        loginButton.setBounds(110, 100, 80, 20);

        panel.add(userField);
        panel.add(passField);
        panel.add(loginButton);

        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        actionLogin();
    }

    public void actionLogin() {

        loginButton.addActionListener(ae -> {


            String username = userField.getText();
            char[] password = passField.getPassword();


            //get kogin dat
            HashMap<String, String> loginDetails = new HashMap<>();
            loginDetails.put("username", username);
            loginDetails.put("password", Arrays.toString(password));

            //create request
            Request loginRequest = new Request(LOGIN, loginDetails, null);

            // Send request to server e.g. output.writeObject(loginRequest)

            // use global input stream, this is just to show how it works
            ObjectInputStream inputObject = new ObjectInputStream(null);
            Response response = null;

            try {
                response = ((Response) inputObject.readObject());
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Cannot connect to server");
                userField.setText("");
                passField.setText("");
                userField.requestFocus();
                return false;
            }

            // check status of response
            boolean status = response.isStatus();

            if (!status) {
                String errorMsg = (String) response.getData();
                JOptionPane.showMessageDialog(this, errorMsg);
                userField.setText("");
                passField.setText("");
                userField.requestFocus();
                return false;
                // return some error response if status is false
            }

            // if status == true, get session object Session session = response.getData()
            Session session = (Session) response.getData();

            // Save session object and move onto next screen
        });
    }
}