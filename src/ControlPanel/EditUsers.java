package ControlPanel;

import Client.ClientConnector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A class to represent an "Edit Users" page which is bound to EditUsers.form
 */
public class EditUsers {
    protected JPanel editUsersPanel;
    private JPanel editPanel;
    private JPanel optionPanel;
    private JLabel editUsersLabel;
    private JButton createNewUserButton;
    private JButton editExistingUserButton;
    private JButton backButton;
    private JPanel titlePanel;

    /**
     * The edit users functionality packed into the given frame.
     * @param frame The given frame.
     */
    public EditUsers(JFrame frame, ClientConnector connector) {
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
        editExistingUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new EditExistingUser(frame, connector).editExistingUserPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
