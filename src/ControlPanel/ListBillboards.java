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
    private JButton previewContentsButton;
    private JButton editBillboardButton;
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

        previewContentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Billboard billboard = billboardList.get(billboardJList.getSelectedIndex());
                previewBillboardContentsFrame.setContentPane(new PreviewBillboardContents(billboard).previewBillboardContentsPanel);
                previewBillboardContentsFrame.pack();
                previewBillboardContentsFrame.setLocationRelativeTo(frame);
                previewBillboardContentsFrame.setVisible(true);
            }
        });

        editBillboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Billboard billboard = billboardList.get(billboardJList.getSelectedIndex());
                frame.setContentPane(new EditBillboard(frame, connector, billboardList, billboard).editBillboardPanel);
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
            model.addElement(billboard.getBillboardName());
        }

        billboardJList.setModel(model);

        previewBillboardContentsFrame = new JFrame();
        previewBillboardContentsFrame.setPreferredSize(new Dimension(400, 400));
    }
}