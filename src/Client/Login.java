package Client;

import Server.*;
import SocketCommunication.Credentials;

import javax.swing.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {

    JPanel panel = new JPanel();
    JTextField userField = new JTextField(15);
    JPasswordField passField = new JPasswordField(15);
    JButton loginButton = new JButton("Login");

    Login() {
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

          Credentials credentials ;


            try {


                if (username!= null && password != null) {

                    //only create credential object when values are not null

                    credentials = new Credentials(username,password.toString());

                    if (AuthenticationHandler.Authenticate(credentials)) {
                        //in this case enter when at least one result comes it means user is valid
                        OptionMenu optionMenuPanel = new OptionMenu(Authenticate.user);
                        optionMenuPanel.setVisible(true);
                        dispose();
                    } else {
                        //in this case enter when  result size is zero  it means user is invalid
                        JOptionPane.showMessageDialog(this, "Wrong Password / Username");
                        userField.setText("");
                        passField.setText("");
                        userField.requestFocus();
                    }
                }

            } catch (IOException | SQLException err ) {
                JOptionPane.showMessageDialog(this, err.getMessage());
            }


        });
    }
}