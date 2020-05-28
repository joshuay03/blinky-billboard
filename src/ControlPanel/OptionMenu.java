package ControlPanel;

import Client.ClientConnector;
import SocketCommunication.Request;
import SocketCommunication.Response;
import SocketCommunication.Session;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import static SocketCommunication.ServerRequest.*;

/**
 * A class to represent an "Option Menu" page which is bound to OptionMenu.form
 * @author Joshua Young
 */
public class OptionMenu {
    protected JPanel optionMenuPanel;
    protected JButton createButton;
    protected JButton listButton;
    protected JButton scheduleButton;
    protected JButton editButton;
    protected JButton backButton;
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


        backButton.addActionListener(new ActionListener() {
            /**
             * Invoked when the back button is clicked.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new Login(frame, connector).loginPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
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
                Response res = null;
                try {
                    res = new Request(LIST_BILLBOARD, null, connector.session).Send(connector);
                } catch (IOException eo) {
                    eo.printStackTrace();
                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        if(connector.session.canCreateBillboards) {
            createButton.setVisible(true);
        }
        else {
            createButton.setVisible(false);
        }
        if(connector.session.scheduleBillboards) {
            scheduleButton.setVisible((true));
        }
        else {
            scheduleButton.setVisible(false);
        }
        if(connector.session.editUsers) {
            editButton.setVisible(true);
        }
        else {
            editButton.setVisible(false);
        }

    }
}