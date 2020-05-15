package ControlPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ScheduleBillboards {
    protected JPanel scheduleBillboardsPanel;
    protected JPanel optionPanel;
    protected JPanel schedulePanel;
    protected JTable scheduleTable;

    public ScheduleBillboards(JFrame frame) {
    }

    private void createUIComponents() {
        String[] columns = new String[] {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        Object[][] data = new Object[][] {
                {"00:00 - 01:00", "", "", "", "", "", ""},
                {"01:00 - 02:00", "", "", "", "", "", ""},
                {"02:00 - 03:00", "Billboard Name", "Billboard Name", "Billboard Name", "Billboard Name", "Billboard Name", "Billboard Name"}
        };
        DefaultTableModel model = new DefaultTableModel(data, columns);
        scheduleTable = new JTable(model);
    }
}
