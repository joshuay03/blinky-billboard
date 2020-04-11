package Client;

import javax.swing.*;

public class OptionMenu extends JFrame {

    JLabel welcome = new JLabel("OptionMenu Frame");
    JPanel panel = new JPanel();

    OptionMenu() {
        super("Option Menu");
        setSize(300, 200);
        setLocation(500, 280);
        panel.setLayout(null);

        welcome.setBounds(70, 50, 150, 60);

        panel.add(welcome);

        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}