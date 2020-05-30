package ControlPanel;

import BillboardSupport.Billboard;
import Client.ClientConnector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

public class ListBillboards {
    protected JPanel listBillboardsPanel;
    protected JPanel listPanel;
    protected JButton backButton;
    protected JLabel listBillboardsLabel;
    protected ClientConnector connector;
    protected Billboard billboard;

    protected List<Billboard> billboardList;
    protected JList<String> billboardJList;
    protected DefaultListModel<String> model;

    protected JFrame previewBillboardContentsFrame;

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

        billboardJList.addListSelectionListener(new ListSelectionListener() {
            /**
             * Called whenever the value of the selection changes.
             *
             * @param e the event that characterizes the change.
             */
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Billboard billboard = billboardList.get(billboardJList.getSelectedIndex());
                previewBillboardContentsFrame.setContentPane(new PreviewBillboardContents(billboard).previewBillboardContentsPanel);
                previewBillboardContentsFrame.pack();
                previewBillboardContentsFrame.setLocationRelativeTo(frame);
                previewBillboardContentsFrame.setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        billboardJList = new JList<>();
        model = new DefaultListModel<>();

        for (Billboard billboard : billboardList) {
            model.addElement(billboard.getBillboardName());
        }

        billboardJList.setModel(model);

        previewBillboardContentsFrame = new JFrame();
        previewBillboardContentsFrame.setPreferredSize(new Dimension(200, 200));
    }
}