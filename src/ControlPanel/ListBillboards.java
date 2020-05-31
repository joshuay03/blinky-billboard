package ControlPanel;

import BillboardSupport.Billboard;
import Client.ClientConnector;
import SocketCommunication.Request;
import SocketCommunication.Response;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.util.List;

public class ListBillboards {
    protected JPanel listBillboardsPanel;
    protected JPanel titlePanel;
    protected JButton backButton;
    protected JLabel listBillboardsLabel;
    protected JPanel listPanel;
    protected JList<String> billboardJList;
    protected JButton previewContentsButton;
    protected JButton editBillboardButton;
    protected JButton deleteBillboardButton;
    protected JFrame previewBillboardContentsFrame;

    protected List<Billboard> billboardList;
    protected DefaultListModel<String> model;
    protected Billboard billboard;

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
                previewBillboardContentsFrame.setTitle("Preview contents: " + billboard.getBillboardName());
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
        deleteBillboardButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Billboard billboard = billboardList.get(billboardJList.getSelectedIndex());

                Request deleteBillboard = Request.deleteBillboardReq(billboard.getBillboardName(), connector.session);

                Response response;

                try {
                    response = deleteBillboard.Send(connector);
                } catch (IOException excep) {
                    JOptionPane.showMessageDialog(null, "Cannot delete billboard");
                    return;
                }

                // check status of response
                boolean status = response.isStatus();

                if (!status) {
                    String errorMsg = (String) response.getData();
                    JOptionPane.showMessageDialog(null, "Cannot delete billboard. Error: " + errorMsg);
                }

                if (status) {
                    billboardList.remove(billboard);
                    model.removeElement(billboard.getBillboardName());
                    billboardJList.setModel(model);
                    JOptionPane.showMessageDialog(null, "Billboard successfully deleted.");
                }

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
        previewBillboardContentsFrame.setPreferredSize(new Dimension(600, 600));
    }
}