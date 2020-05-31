package ControlPanel;

import BillboardSupport.Billboard;
import Client.ClientConnector;
import SocketCommunication.Request;
import SocketCommunication.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent an "Option Menu" page which is bound to OptionMenu.form
 */
public class OptionMenu implements Runnable {
    protected JPanel optionMenuPanel;
    protected JButton createButton;
    protected JButton listButton;
    protected JButton scheduleButton;
    protected JButton editButton;
    protected JButton logoutButton;
    protected JPanel titlePanel;
    protected JPanel optionsPanel;
    protected ClientConnector connector;

    /**
     *
     * @param frame: JPanel Frame
     * @param connector: client connector object initialized when the client makes a connection with the server.
     */
    public OptionMenu(JFrame frame, ClientConnector connector) {
        this.connector = connector;

        run();

        logoutButton.addActionListener(new ActionListener() {
            /**
             * Invoked when the back button is clicked.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Response logoutResp = null;
                try {
                    logoutResp = Request.logoutReq(connector.session).Send(connector);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if(logoutResp.isStatus() == true) {
                    connector.session = null;
                    frame.setContentPane(new Login(frame, connector).loginPanel);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);

                    JOptionPane.showMessageDialog(frame, "Logout complete");
                } else {
                    JOptionPane.showMessageDialog(frame, "Logout was unsuccessful");
                }
            }
        });

        createButton.addActionListener(new ActionListener() {
            /**
             * Invoked when the create billboards button is clicked.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                    frame.setContentPane(new CreateBillboards(frame, connector).createBillboardsPanel);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
            }
        });

        scheduleButton.addActionListener(new ActionListener() {
            /**
             * Invoked when the schedule billboard button is clicked.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new ScheduleBillboards(frame, connector).scheduleBillboardsPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        editButton.addActionListener(new ActionListener() {
            /**
             * Invoked when the edit billboard button is clicked.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new EditUsers(frame, connector).editUsersPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        listButton.addActionListener(new ActionListener() {
            /**
             * Invoked when the list billboard button is clicked.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
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
                    JOptionPane.showMessageDialog(null, response.getData());
                    // return some error response if status is false
                }

                // If the query succeeded, show the billboard list
                if (status) {
                    List<?> billboardObjects = (List<?>) response.getData();

                    List<Billboard> billboardList = new ArrayList<>();
                    billboardObjects.forEach(x -> billboardList.add((Billboard) x));

                    frame.setContentPane(new ListBillboards(frame, connector, billboardList).listBillboardsPanel);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            }
        });
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        createButton.setVisible(connector.session.canCreateBillboards);
        scheduleButton.setVisible(connector.session.scheduleBillboards);
        editButton.setVisible(connector.session.editUsers);

    }
}