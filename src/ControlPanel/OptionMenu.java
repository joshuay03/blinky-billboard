package ControlPanel;

import BillboardSupport.Billboard;
import BillboardSupport.Schedule;
import Client.ClientConnector;
import SocketCommunication.Request;
import SocketCommunication.Response;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent an "Option Menu" page which is bound to OptionMenu.form
 */
public class OptionMenu {
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
     * Get a list of all billboards currently contained within the database.
     * @return A list of billboards.
     */
    private List<Billboard> getBillboards() {
        List<Billboard> billboards = new ArrayList<>();

        Request listRequest = Request.listAllBillboardsReq(connector.session);

        // Send request to server
        Response response;
        // use global input stream, this is just to show how it works

        try {
            response = listRequest.Send(connector);
        } catch (IOException excep) {
            JOptionPane.showMessageDialog(null, "Cannot connect to server");
            return billboards;
        }

        // check status of response
        boolean status = response.isStatus();

        if (!status) {
            String errorMsg = (String) response.getData();
            JOptionPane.showMessageDialog(null, errorMsg);
            return billboards;
        }


        List<?> billboardObjects = (List<?>) response.getData();

        billboardObjects.forEach(x -> billboards.add((Billboard) x));
        return billboards;

    }

    /**
     * A form containing the UI i.e buttons for the user to use.
     * @param frame:     JPanel Frame
     * @param connector: client connector object initialized when the client makes a connection with the server.
     */
    public OptionMenu(JFrame frame, ClientConnector connector) {
        this.connector = connector;



        logoutButton.addActionListener(new ActionListener() {
            /**
             * Invoked when the back button is clicked.
             * Logout button.
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] buttons = { "Yes", "No" };
                int returnValue = JOptionPane.showOptionDialog(frame, "Are you sure you want to logout?", "Confirm Logout",
                        JOptionPane.WARNING_MESSAGE, 0, null, buttons, buttons[1]);
                if (returnValue == 0) {
                    Response logoutResp = null;
                    try {
                        logoutResp = Request.logoutReq(connector.session).Send(connector);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    connector.session = null;
                    frame.setContentPane(new Login(frame, connector).loginPanel);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            }
        });

        createButton.addActionListener(new ActionListener() {
            /**
             * Invoked when the create billboards button is clicked.
             * Instantiate the createBillboard panel
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
             * Make a request for currently scheduled billboards.
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Schedule> schedule = new ArrayList<>();

                Request listRequest = Request.viewCurrentlyScheduledBillboardReq(connector.session);

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
                    return;
                }

                List<?> scheduleObjects = (List<?>) response.getData();

                scheduleObjects.forEach(x -> schedule.add((Schedule) x));

                List<Billboard> billboardList = getBillboards();

                frame.setContentPane(new ScheduleBillboards(frame, connector, schedule, billboardList).scheduleBillboardsPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        editButton.addActionListener(new ActionListener() {
            /**
             * Invoked when the edit billboard button is clicked.
             * Instantiate the editUsers panel
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
             * Instantiate the listBillboards panel.
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {

                List<Billboard> billboardList = getBillboards();

                frame.setContentPane(new ListBillboards(frame, connector, billboardList).listBillboardsPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            }
        });
    }

    private void createUIComponents() {
        createButton = new JButton();
        scheduleButton = new JButton();
        editButton = new JButton();
        createButton.setEnabled(connector.session.canCreateBillboards);
        scheduleButton.setEnabled(connector.session.scheduleBillboards);
        editButton.setEnabled(connector.session.editUsers);
    }
}