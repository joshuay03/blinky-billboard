package Client;

import SocketCommunication.Request;
import static SocketCommunication.ServerRequest.*;

import SocketCommunication.Response;

import javax.swing.*;

import java.io.IOException;

public class OptionMenu extends JFrame {

    JPanel panel = new JPanel();
    JButton listAllBillboardsButton = new JButton("List All Billboards");
    JButton createBillboardsButton = new JButton("Create Billboards");
    JButton editAllBillboardsButton = new JButton("Edit All Billboards");
    JButton scheduleBillboardsButton = new JButton("Schedule Billboards");
    JButton editUsersButton = new JButton("Edit Users");

    public OptionMenu(ClientConnector connector) {
        super("Option Menu");
        setSize(300, 200);
        setLocation(500, 280);
        panel.setLayout(null);


        listAllBillboardsButton.setBounds(70, 25, 150, 30);
        panel.add(listAllBillboardsButton);
        actionListBillboards(connector);

            if (connector.session.canCreateBillboards) {
                createBillboardsButton.setBounds(70, 25, 150, 30);
                panel.add(createBillboardsButton);
                actionCreateBillboards(connector);
            }
            else if (connector.session.editAllBillboards) {
                editAllBillboardsButton.setBounds(70, 50, 150, 30);
                panel.add(editAllBillboardsButton);
                actionEditAllBillboards(connector);
            }
            else if (connector.session.scheduleBillboards) {
                scheduleBillboardsButton.setBounds(70, 75, 150, 30);
                panel.add(scheduleBillboardsButton);
                actionScheduleBillboards(connector);
            }
            else if (connector.session.editUsers) {
                editUsersButton.setBounds(70, 100, 150, 30);
                panel.add(editUsersButton);
                actionEditUsers(connector);

        }

        getContentPane().add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionListBillboards(ClientConnector connector) {
        listAllBillboardsButton.addActionListener(ae -> {
            System.out.println("Testing list billboards button");
            Response res = null;
            try {
                res = new Request(LIST_BILLBOARD, null, connector.session).Send(connector);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void actionCreateBillboards(ClientConnector connector) {
        createBillboardsButton.addActionListener(ae -> {
            System.out.println("Testing create billboards button");
            //This will take you to the Create billboard GUI
            //Path to create billboards GUI here, GUI once submit button clicked will send createBillBoard request
        });
    }
    public void actionScheduleBillboards(ClientConnector connector) {
        scheduleBillboardsButton.addActionListener(ae -> {
            System.out.println("Testing Schedule Billboards button");
        });
    }

    public void actionEditAllBillboards(ClientConnector connector) {
        editAllBillboardsButton.addActionListener(ae -> {
            System.out.println("Testing edit all billboards button");
        });
    }

    public void actionEditUsers(ClientConnector connector) {
        editUsersButton.addActionListener(ae -> {
            System.out.println("Testing edit users button");
            //Call the list, create, and edit users GUI
        });
    }
}