package ControlPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatePicker {
    int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
    int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    JLabel label = new JLabel("", JLabel.CENTER);
    String day = "";
    JDialog dialog;
    JButton[] button = new JButton[49];

    public DatePicker(JFrame parent) {
        dialog = new JDialog();
        dialog.setModal(true);
        String[] header = {"Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};
        JPanel displayPanel = new JPanel(new GridLayout(7, 7));
        displayPanel.setPreferredSize(new Dimension(430, 120));

        for (int i = 0; i < button.length; i++) {
            final int selection = i;
            button[i] = new JButton();
            button[i].setFocusPainted(false);
            button[i].setBackground(Color.white);
            if (i > 6)
                button[i].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        day = button[selection].getActionCommand();
                        dialog.dispose();
                    }
                });
            if (i < 7) {
                button[i].setText(header[i]);
                button[i].setForeground(Color.red);
            }

            displayPanel.add(button[i]);
        }

        JPanel optionPanel = new JPanel(new GridLayout(1, 3));
        JButton previous = new JButton("Previous");
        previous.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                month--;
                displayDate();
            }
        });

        optionPanel.add(previous);
        optionPanel.add(label);
        JButton next = new JButton("Next");
        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                month++;
                displayDate();
            }
        });
        optionPanel.add(next);

        dialog.add(displayPanel, BorderLayout.CENTER);
        dialog.add(optionPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        displayDate();
        dialog.setVisible(true);
    }

    public void displayDate() {
        for (int x = 7; x < button.length; x++) {
            button[x].setText("");
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "MMMM yyyy");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, 1);
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        for (int x = 6 + dayOfWeek, day = 1; day <= daysInMonth; x++, day++)
            button[x].setText("" + day);
        label.setText(sdf.format(cal.getTime()));
        dialog.setTitle("Date Picker");
    }

    public String setPickedDate() {
        if (day.equals(""))
            return day;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "dd-MM-yyyy");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, Integer.parseInt(day));
        return sdf.format(cal.getTime());
    }
}