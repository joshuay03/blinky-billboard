package ControlPanel;

import Client.ClientConnector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Schedule {
    protected JPanel schedulePanel;
    protected JPanel datePanel;
    protected JLabel scheduledDateLabel;
    protected JTextField dateTextField;
    protected JButton selectDateButton;

    Schedule(JFrame scheduleFrame, ClientConnector connector) {

        selectDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateTextField.setText(new DatePicker(scheduleFrame).setPickedDate());
            }
        });
    }
}
