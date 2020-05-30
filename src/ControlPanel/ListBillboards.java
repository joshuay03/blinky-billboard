package ControlPanel;

import BillboardSupport.Billboard;
import Client.ClientConnector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ListBillboards {
    protected JPanel listBillboardsPanel;
    protected JPanel listPanel;
    protected JButton backButton;
    protected JLabel listBillboardsLabel;
    protected ClientConnector connector;

    protected List<Billboard> billboardList;
    protected JList<Billboard> billboardJList;
    DefaultListModel<Billboard> model;

    public ListBillboards(JFrame frame, ClientConnector connector, List<Billboard> billboardList) {
        this.billboardList = billboardList;

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
    }

    private void createUIComponents() {
        billboardJList = new JList<>();
        model = new DefaultListModel<>();

        for (Billboard billboard : billboardList) {
            model.addElement(billboard);
        }

        billboardJList.setModel(model);
    }
}