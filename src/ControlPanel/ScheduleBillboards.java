package ControlPanel;

import BillboardSupport.Billboard;
import BillboardSupport.Occurrence;
import Client.ClientConnector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
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
     *
     * @param frame      A JFrame object
     * @param connector  A ClientConnector object
     * @param schedule   A list of schedules
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
        List<List<String>> data = new ArrayList<>();
        List<List<Occurrence>> occurrenceData = new ArrayList<>();

        for (int i = 0; i < daysInAWeek; i++) {
            data.add(new ArrayList<>());
            occurrenceData.add(new ArrayList<>());

            daysOfTheWeek[i] = simpleDateformat.format(calendar.getTime());
            data.get(i).add(daysOfTheWeek[i]);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        calendar.add(Calendar.DAY_OF_YEAR, -1); // as inclusive of today

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 23, 59, 59);

        Collections.sort(schedule);
        // extrapolate schedules over the next week
        schedule.forEach(x -> {
            List<Occurrence> occurrences = Arrays.asList(x.extrapolate(new Timestamp(calendar.getTime().getTime())));
            occurrences.forEach(o -> {
                Calendar cal = Calendar.getInstance();
                int d = cal.get(Calendar.DATE);
                int m = cal.get(Calendar.MONTH);
                int y = cal.get(Calendar.YEAR);
                cal.set(y, m, d, 23, 59, 59);
                for (int i = 0; i < daysInAWeek - 1; i++) {
                    Timestamp endOfday = new Timestamp(cal.getTime().getTime());
                    Timestamp startOfDay = new Timestamp(endOfday.getTime() - (24 * 60 * 60 * 1000));
                    if (startOfDay.before(o.start) && endOfday.after(o.start)) {
                        occurrenceData.get(i).add(o);
                    }
                    cal.add(Calendar.DATE, i);
                }
            });
        });

        DefaultTableModel model = new DefaultTableModel(0, 0);
        DateFormat format = new SimpleDateFormat("HH:mm");

        for (int i =0; i < 7; i++) {
            List<Occurrence> dayData = occurrenceData.get(i);
            Collections.sort(dayData);
            for (Occurrence o : dayData) {
                data.get(i).add(o.name + " " + format.format(o.start) + "-" + format.format(o.end));
            }
            model.addColumn(daysOfTheWeek[i], data.get(i).toArray());
        }

        scheduleTable = new JTable(model);

        Font defaultFont = scheduleTable.getFont();
        defaultFont = defaultFont.deriveFont(20.0f);

        scheduleFrame = new JFrame("Schedule a Billboard");
        scheduleFrame.setPreferredSize(new Dimension(600, 300));
    }
}

