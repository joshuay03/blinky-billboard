package Client;

import javax.swing.*;

public class OptionMenu extends JFrame {

    JPanel panel = new JPanel();
    JButton listAllBillboardsButton = new JButton("List All Billboards");
    JButton createBillboardsButton = new JButton("Create Billboards");
    JButton editAllBillboardsButton = new JButton("Edit All Billboards");
    JButton scheduleBillboardsButton = new JButton("Schedule Billboards");
    JButton editUsersButton = new JButton("Edit Users");

    OptionMenu(ClientUser user) {
        super("Option Menu");
        setSize(300, 200);
        setLocation(500, 280);
        panel.setLayout(null);

        listAllBillboardsButton.setBounds(70, 25, 150, 30);
        panel.add(listAllBillboardsButton);
        actionListBillboards();

        for (String permission : user.permissions) {
            if (permission.equals("Create Billboards")) {
                createBillboardsButton.setBounds(70, 25, 150, 30);
                panel.add(createBillboardsButton);
                actionCreateBillboards();
            }
            else if (permission.equals("Edit All Billboards")) {
                editAllBillboardsButton.setBounds(70, 50, 150, 30);
                panel.add(editAllBillboardsButton);
                actionEditAllBillboards();
            }
            else if (permission.equals("Schedule Billboards")) {
                scheduleBillboardsButton.setBounds(70, 75, 150, 30);
                panel.add(scheduleBillboardsButton);
                actionScheduleBillboards();
            }
            else if (permission.equals("Edit Users")) {
                editUsersButton.setBounds(70, 100, 150, 30);
                panel.add(editUsersButton);
                actionEditUsers();
            }
        }

        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionListBillboards() {
        listAllBillboardsButton.addActionListener(ae -> {
            System.out.println("Testing list billboards button");
        });
    }
    public void actionScheduleBillboards() {
        scheduleBillboardsButton.addActionListener(ae -> {
            System.out.println("Testing Schedule Billboards button");
        });
    }

    public void actionEditAllBillboards() {
        editAllBillboardsButton.addActionListener(ae -> {
            System.out.println("Testing edit all billboards button");
        });
    }

    public void actionCreateBillboards() {
        createBillboardsButton.addActionListener(ae -> {
            System.out.println("Testing create billboards button");
        });
    }

    public void actionEditUsers() {
        editUsersButton.addActionListener(ae -> {
            System.out.println("Testing edit users button");
        });
    }
}