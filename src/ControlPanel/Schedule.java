package ControlPanel;

import Client.ClientConnector;
import SocketCommunication.Request;
import SocketCommunication.Response;
import SocketCommunication.Session;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Schedule {
    protected JPanel schedulePanel;
    protected JPanel datePanel;
    protected JLabel dateLabel;
    protected JTextField dateTextField;
    protected JButton selectDateButton;
    protected JComboBox hourComboBox;
    protected JComboBox minuteComboBox;
    protected JFormattedTextField durationFormattedTextField;
    protected JLabel selectDurationLabel;
    protected JPanel timeDurationPanel;
    protected JRadioButton dailyRadioButton;
    protected JRadioButton customMinutesRadioButton;
    protected JRadioButton hourlyRadioButton;
    protected JPanel frequencyPanel;
    protected JFormattedTextField customFrequencyFormattedTextField;
    private JButton doneButton;

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

        NumberFormat numFormat = new DecimalFormat("#");

        NumberFormatter durationFormatter  = new NumberFormatter(numFormat);
        durationFormatter.setMinimum(1);
        DefaultFormatterFactory durationFactory = new DefaultFormatterFactory(durationFormatter);
        durationFormattedTextField.setFormatterFactory(durationFactory);
        durationFormattedTextField.setValue(1);

        NumberFormatter customFrequencyFormatter  = new NumberFormatter(numFormat);
        customFrequencyFormatter.setMinimum(1);
        customFrequencyFormatter.setMaximum((int) durationFormattedTextField.getValue());
        DefaultFormatterFactory customFrequencyFactory = new DefaultFormatterFactory(customFrequencyFormatter);
        customFrequencyFormattedTextField.setFormatterFactory(customFrequencyFactory);
        customFrequencyFormattedTextField.setValue(1);

        selectDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateTextField.setText(new DatePicker(scheduleFrame).setPickedDate());
            }
        });

        durationFormattedTextField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                customFrequencyFormatter.setMaximum((int) durationFormattedTextField.getValue());
                DefaultFormatterFactory customFrequencyFactory = new DefaultFormatterFactory(customFrequencyFormatter);
                customFrequencyFormattedTextField.setFormatterFactory(customFrequencyFactory);
            }
        });

        dailyRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hourlyRadioButton.setSelected(false);
                customMinutesRadioButton.setSelected(false);
            }
        });

        hourlyRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dailyRadioButton.setSelected(false);
                customMinutesRadioButton.setSelected(false);
            }
        });

        customMinutesRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dailyRadioButton.setSelected(false);
                hourlyRadioButton.setSelected(false);
            }
        });
        doneButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
//                Schedule schedule = new BillboardSupport.Schedule();
                //create request
//                Request loginRequest = Request.scheduleBillboardReq();

                // Send request to server
//                Response response;
//                // use global input stream, this is just to show how it works
//
//                try {
//                    response = loginRequest.Send(connector);
//                } catch (IOException excep) {
//                    JOptionPane.showMessageDialog(null, "Cannot connect to server");
//                    usernameField.setText("");
//                    passwordField.setText("");
//                    usernameField.requestFocus();
//                    return;
//                }
//
//                // check status of response
//                boolean status = response.isStatus();
//
//                if (!status) {
//                    String errorMsg = (String) response.getData();
//                    JOptionPane.showMessageDialog(null, errorMsg);
//                    usernameField.setText("");
//                    passwordField.setText("");
//                    usernameField.requestFocus();
//                    // return some error response if status is false
//                }
//
//                // if status == true, get session object Session session = response.getData()
//                if (status) {
//                    // Save session object and move onto next screen
//                    connector.session = (Session) response.getData();
//                    frame.setContentPane(new OptionMenu(frame, connector).optionMenuPanel);
//                    frame.pack();
//                    frame.setLocationRelativeTo(null);
//                    frame.setVisible(true);
//                }
            }
        });
    }
}
