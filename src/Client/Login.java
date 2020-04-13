package Client;

import javax.swing.*;

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
            String user = userField.getText();
            char[] pass = passField.getPassword();
            if (user.equals("josh") && String.valueOf(pass).equals("123")) {
                JButton[] buttons = new JButton[1];
                JButton buttonTest = new JButton("Test");
                buttons[0] = buttonTest;
                OptionMenu optionMenuPanel = new OptionMenu(buttons);
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