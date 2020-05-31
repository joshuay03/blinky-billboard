package ControlPanel;

import BillboardSupport.Billboard;
import Client.ClientConnector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    protected List<BillboardSupport.Schedule> schedule;

    protected JFrame scheduleFrame;

    /**
     * Set the schedule of all billboards given a list of all schedules and billboards
     * @param frame A JFrame object
     * @param connector A ClientConnector object
     * @param schedule A list of schedules
     * @param billboards A list of billboards
     */
    public ScheduleBillboards(JFrame frame, ClientConnector connector, List<BillboardSupport.Schedule> schedule, List<Billboard> billboards) {
        this.schedule = schedule;

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
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                scheduleFrame.setContentPane(new Schedule(scheduleFrame, connector, billboards).schedulePanel);
                scheduleFrame.pack();
                scheduleFrame.setLocationRelativeTo(frame);
                scheduleFrame.setVisible(true);
            }
        });
    }

    /**
     * Instantiate the UI components for the form.
     */
    private void createUIComponents() {
        final int daysInAWeek = 7;
        String[] daysOfTheWeek = new String[daysInAWeek];

        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < daysInAWeek; i++) {
            daysOfTheWeek[i] = simpleDateformat.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Object[][] scheduledBillboards = new Object[][] {
                {daysOfTheWeek[0]},
                {daysOfTheWeek[1]},
                {daysOfTheWeek[2]},
                {daysOfTheWeek[3]},
                {daysOfTheWeek[4]},
                {daysOfTheWeek[5]},
                {daysOfTheWeek[6]}
        };


        DefaultTableModel model = new DefaultTableModel(scheduledBillboards, daysOfTheWeek);
        scheduleTable = new JTable(model);

        scheduleFrame = new JFrame("Schedule a Billboard");
        scheduleFrame.setPreferredSize(new Dimension(600, 300));
    }
}

