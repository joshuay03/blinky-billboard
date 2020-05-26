package ControlPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A class to represent an "Edit Users" page which is bound to EditUsers.form
 * @author Joshua Young
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
     *
     * @param frame
     */
    public EditUsers(JFrame frame) {
        backButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new OptionMenu(frame).optionMenuPanel);
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
                frame.setContentPane(new CreateNewUser(frame).createNewUserPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
