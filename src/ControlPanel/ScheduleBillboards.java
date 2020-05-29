package ControlPanel;

import Client.ClientConnector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A class to represent a "Schedule Billboards" page which is bound to ScheduleBillboards.form
 */
public class ScheduleBillboards {
    protected JPanel scheduleBillboardsPanel;
    protected JPanel schedulePanel;
    protected JLabel scheduledDateLabel;
    protected JTable scheduleTable;
    protected JPanel titlePanel;
    protected JButton backButton;
    protected JButton scheduleButton;

    protected JFrame scheduleFrame;

    /**
     *
     * @param frame
     */
    public ScheduleBillboards(JFrame frame, ClientConnector connector) {
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

        scheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scheduleFrame.setContentPane(new Schedule(scheduleFrame, connector).schedulePanel);
                scheduleFrame.pack();
                scheduleFrame.setVisible(true);
            }
        });
    }

    /**
     *
     */
    private void createUIComponents() {
        String[] columns = new String[] {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        Object[][] data = new Object[][] {
                {"00:00 - 01:00", "Billboard Name", "Billboard Name", "Billboard Name", "Billboard Name", "Billboard Name", "Billboard Name"},
                {"01:00 - 02:00", "", "", "", "", "", ""},
                {"02:00 - 03:00", "", "", "", "", "", ""},
                {"03:00 - 04:00", "", "", "", "", "", ""},
                {"04:00 - 05:00", "", "", "", "", "", ""},
                {"05:00 - 06:00", "", "", "", "", "", ""},
                {"06:00 - 07:00", "", "", "", "", "", ""},
                {"07:00 - 08:00", "", "", "", "", "", ""},
                {"08:00 - 09:00", "", "", "", "", "", ""},
                {"09:00 - 10:00", "", "", "", "", "", ""},
                {"10:00 - 11:00", "", "", "", "", "", ""},
                {"11:00 - 12:00", "", "", "", "", "", ""},
                {"12:00 - 13:00", "", "", "", "", "", ""},
                {"13:00 - 14:00", "", "", "", "", "", ""},
                {"14:00 - 15:00", "", "", "", "", "", ""},
                {"15:00 - 16:00", "", "", "", "", "", ""},
                {"16:00 - 17:00", "", "", "", "", "", ""},
                {"17:00 - 18:00", "", "", "", "", "", ""},
                {"18:00 - 19:00", "", "", "", "", "", ""},
                {"19:00 - 20:00", "", "", "", "", "", ""},
                {"20:00 - 21:00", "", "", "", "", "", ""},
                {"21:00 - 22:00", "", "", "", "", "", ""},
                {"22:00 - 23:00", "", "", "", "", "", ""},
                {"23:00 - 24:00", "", "", "", "", "", ""},
        };
        DefaultTableModel model = new DefaultTableModel(data, columns);
        scheduleTable = new JTable(model);

        scheduleFrame = new JFrame("Schedule a Billboard");
        scheduleFrame.setPreferredSize(new Dimension(600, 300));
    }
}

