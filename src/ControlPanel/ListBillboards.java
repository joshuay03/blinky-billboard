package ControlPanel;

import BillboardSupport.Billboard;
import Client.ClientConnector;
import SocketCommunication.Request;
import SocketCommunication.Response;
import SocketCommunication.Session;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static SocketCommunication.ServerRequest.LIST_BILLBOARDS;
import static SocketCommunication.ServerRequest.LOGIN;

public class ListBillboards {
    protected JList<Billboard> billboardList;
    protected JPanel listBillboardsPanel;
    protected JPanel listPanel;
    protected JButton backButton;
    private JLabel listBillboardsLabel;
    protected JFrame frame;
    protected  ClientConnector connector;
    DefaultListModel<Billboard> model = new DefaultListModel<>();

    public ListBillboards(JFrame frame, ClientConnector connector) {
        this.frame = frame;
        this.connector = connector;

        backButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new OptionMenu(frame, connector).optionMenuPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }


    private void createUIComponents() {
        Request listRequest = Request.listAllBillboardsReq(connector.session);

        // Send request to server
        Response response;
        // use global input stream, this is just to show how it works

        try {
            response = listRequest.Send(connector);
        } catch (IOException excep) {
            JOptionPane.showMessageDialog(null, "Cannot connect to server");
            return;
        }


        // check status of response
        boolean status = response.isStatus();

        if (!status) {
            String errorMsg = (String) response.getData();
            JOptionPane.showMessageDialog(null, errorMsg);
            // return some error response if status is false
        }


        if (status) {

            Billboard[] billboardListServer =  ((Billboard[])response.getData());
            billboardList.setModel(model);

            for (Billboard b : billboardListServer) {
                model.addElement(b);
            }
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }    }
}
