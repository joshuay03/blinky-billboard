package ControlPanel;

import BillboardSupport.Billboard;
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
 * A class to represent an "Edit Users" page which is bound to EditUsers.form
 */
public class EditUsers {
    protected JPanel editUsersPanel;
    private JPanel editPanel;
    private JLabel editUsersLabel;
    private JButton createNewUserButton;
    private JButton editUserButton;
    private JButton backButton;
    private JPanel titlePanel;
    private JButton deleteUserButton;
    private JList<String> userList;

    protected DefaultListModel<String> model;

    /**
     *  @param frame
     *
     */
    public EditUsers(JFrame frame, ClientConnector connector) {
        Request userRequest = Request.listUsersReq(connector.session);

        // Send request to server
        Response response;
        // use global input stream, this is just to show how it works

        try {
            response = userRequest.Send(connector);
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


        List<?> userObjects = (List<?>) response.getData();

        model = new DefaultListModel<>();
        userObjects.forEach(x ->
                model.addElement((String) x)
        );

        userList.setModel(model);


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

        createNewUserButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new CreateNewUser(frame, connector).createNewUserPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        editUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new EditExistingUser(frame, connector).editExistingUserPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        deleteUserButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userList.getSelectedValue();

                Request deleteUser = Request.deleteUserReq(username, connector.session);

                Response response;

                try {
                    response = deleteUser.Send(connector);
                } catch (IOException excep) {
                    JOptionPane.showMessageDialog(null, "Cannot delete user");
                    return;
                }

                // check status of response
                boolean status = response.isStatus();

                if (!status) {
                    String errorMsg = (String) response.getData();
                    JOptionPane.showMessageDialog(null, "Cannot delete user. Error: " + errorMsg);
                }

                if (status) {
                    String msg = (String) response.getData();
                    JOptionPane.showMessageDialog(null, msg);
                }

            }
        });
    }
}
