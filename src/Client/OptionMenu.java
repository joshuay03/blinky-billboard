package Client;

import javax.swing.*;

public class OptionMenu extends JFrame {

    JPanel panel = new JPanel();
    JButton[] buttons;

    OptionMenu(JButton[] buttons) {
        super("Option Menu");
        setSize(300, 200);
        setLocation(500, 280);
        panel.setLayout(null);
        this.buttons = buttons;

        for (JButton button : buttons) {
            button.setBounds(70, 50, 150, 60);
            panel.add(button);
        }

        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

}