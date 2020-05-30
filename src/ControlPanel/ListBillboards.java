package ControlPanel;

import BillboardSupport.Billboard;
import Client.ClientConnector;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ListBillboards {
    protected JPanel listBillboardsPanel;
    protected JPanel listPanel;
    protected JButton backButton;
    protected JLabel listBillboardsLabel;
    protected  ClientConnector connector;

    protected Billboard[] billboardList;
    protected JList<Billboard> billboardJList;
    DefaultListModel<Billboard> model;

    public ListBillboards(JFrame frame, ClientConnector connector, Billboard[] billboardList) {
        this.billboardList = billboardList;

        JFrame previewBillboardContentsFrame = new JFrame("Billboard Contents");
        previewBillboardContentsFrame.setPreferredSize(new Dimension(300, 250));
        previewBillboardContentsFrame.setContentPane(new PreviewBillboardContents(previewBillboardContentsFrame, connector).previewBillboardContentsPanel);
        previewBillboardContentsFrame.pack();
        previewBillboardContentsFrame.setLocationRelativeTo(null);
        previewBillboardContentsFrame.setVisible(true);

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