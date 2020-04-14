package Client;

import javax.swing.*;

public class OptionMenu extends JFrame {

    JPanel panel = new JPanel();
    JButton createBillboardsButton = new JButton("Create Billboards");
    JButton listBillboardsButton = new JButton("List All Billboards");
    JButton scheduleBillboardsButton = new JButton("Schedule Billboards");
    JButton editUsersButton = new JButton("Edit Users");

    OptionMenu(User user) {
        super("Option Menu");
        setSize(300, 200);
        setLocation(500, 280);
        panel.setLayout(null);

        for (String permission : user.permissions) {
            if (permission.equals("Create Billboards")) {
                createBillboardsButton.setBounds(70, 25, 150, 30);
                panel.add(createBillboardsButton);
            }
            else if (permission.equals("List Billboards")) {
                listBillboardsButton.setBounds(70, 50, 150, 30);
                panel.add(listBillboardsButton);
            }
            else if (permission.equals("Schedule Billboards")) {
                scheduleBillboardsButton.setBounds(70, 75, 150, 30);
                panel.add(scheduleBillboardsButton);
                actionScheduleBillboards();
            }
            else if (permission.equals("Edit Users")) {
                editUsersButton.setBounds(70, 100, 150, 30);
                panel.add(editUsersButton);
            }
        }

        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionScheduleBillboards() {
        scheduleBillboardsButton.addActionListener(ae -> {
            System.out.println("Test");
        });
    }
}