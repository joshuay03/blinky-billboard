package Client;

import javax.swing.*;

public class Login extends JFrame {

    public static void main(String[] args) {
        Login frame1 = new Login();
    }

    JPanel panel = new JPanel();
    JTextField userField = new JTextField(15);
    JPasswordField passField = new JPasswordField(15);
    JButton loginButton = new JButton("Client.Login");

    Login() {
        super("Client.Login Authentication");
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
            String user = userField.getText();
            String pass = passField.getText();
            if (user.equals("josh") && pass.equals("12345")) {
                OptionMenu regFace = new OptionMenu();
                regFace.setVisible(true);
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