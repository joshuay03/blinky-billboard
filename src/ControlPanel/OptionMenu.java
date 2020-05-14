package ControlPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionMenu {
    protected JPanel optionMenuPanel;
    protected JButton createButton;
    protected JButton listButton;
    protected JButton scheduleButton;
    protected JButton editButton;

    public OptionMenu(JFrame frame) {
        createButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setContentPane(new CreateBillboards(frame).createBillboardsPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}