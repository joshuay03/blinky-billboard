package Client;

import javax.swing.*;

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
            String username = userField.getText();
            char[] password = passField.getPassword();
            if (username.equals("josh") && String.valueOf(password).equals("123")) {
                OptionMenu optionMenuPanel = new OptionMenu(user);
                optionMenuPanel.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Wrong Password / Username");
                userField.setText("");
                passField.setText("");
                userField.requestFocus();
            }
        });
    }
}