package Client;

import Server.DBProps;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {

    JPanel panel = new JPanel();
    JTextField userField = new JTextField(15);
    JPasswordField passField = new JPasswordField(15);
    JButton loginButton = new JButton("Login");

    Login(User user) {
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
        actionLogin(user);
    }

    public void actionLogin(User user) {

        loginButton.addActionListener(ae -> {

            DBProps props = null;
            Connection dbconn = null;
            try {
                props = new DBProps();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String username = userField.getText();
            char[] password = passField.getPassword();

            try {
                dbconn = DriverManager.getConnection("jdbc:mariadb://" + props.url + ":3306/" + props.schema, props.username, props.password);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (username != null && password != null) {

                    // need to figure out how to unhash password password is currently hardcoded
                    //need a function that hashes and unhashes passcode for create users and login 
                    String sql = String.format("Select * from Users Where user_name= '%s' and password_hash= 'pass123'", username);
                    ResultSet rs = dbconn.createStatement().executeQuery(sql);
                    if (rs.next()) {
                        //in this case enter when at least one result comes it means user is valid
                        OptionMenu optionMenuPanel = new OptionMenu(user);
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

            } catch (SQLException err) {
                JOptionPane.showMessageDialog(this, err.getMessage());
            }


        });
    }
}