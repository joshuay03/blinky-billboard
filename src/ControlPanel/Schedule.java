package ControlPanel;

import Client.ClientConnector;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Schedule {
    protected JPanel schedulePanel;
    protected JPanel datePanel;
    protected JLabel scheduledDateLabel;
    protected JTextField dateTextField;
    protected JButton selectDateButton;
    protected JComboBox hourComboBox;
    protected JComboBox minuteComboBox;
    protected JFormattedTextField durationFormattedTextField;
    protected JLabel selectDurationLabel;

    Schedule(JFrame scheduleFrame, ClientConnector connector) {
        hourComboBox.addItem("Select Hour");
        for (int i = 0; i <= 23; i++) {
            if (i < 10) {
                hourComboBox.addItem("0" + i);
            }
            else {
                hourComboBox.addItem(i);
            }
        }

        minuteComboBox.addItem("Select Minute");
        for (int i = 1; i <= 60; i++) {
            if (i < 10) {
                minuteComboBox.addItem("0" + i);
            }
            else {
                minuteComboBox.addItem(i);
            }
        }


        selectDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateTextField.setText(new DatePicker(scheduleFrame).setPickedDate());
            }
        });

        durationFormattedTextField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
            }
        });
    }

    private void createUIComponents() {
        NumberFormat numFormat = new DecimalFormat("#");
        NumberFormatter numFormatter  = new NumberFormatter(numFormat);
        numFormatter.setMinimum(1);
        durationFormattedTextField = new JFormattedTextField(numFormatter);
        durationFormattedTextField.setValue(1);
    }
}
