package ControlPanel;

import BillboardSupport.Billboard;
import BillboardSupport.DummyBillboards;
import BillboardSupport.RenderedBillboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class CreateBillboards {
    protected JPanel createBillboardsPanel;
    protected JPanel optionPanel;
    protected JPanel createPanel;
    protected JButton viewBillboardButton;
    protected JTextArea messageTextArea;
    protected JFormattedTextField pictureFormattedTextField;
    protected JTextArea informationTextArea;
    protected JLabel messageLabel;
    protected JLabel pictureLabel;
    protected JLabel informationLabel;
    protected JLabel backgroundLabel;
    protected JPanel colourPanel;
    protected ColourChooser colourChooser;

    public CreateBillboards(JFrame frame) {
        viewBillboardButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Billboard billboard = new Billboard(Color.WHITE, Color.BLACK, Color.BLACK, messageTextArea.getText(), informationTextArea.getText(), new ImageIcon(pictureFormattedTextField.getText()), LocalDateTime.now(), 30, 5);
                frame.setContentPane(new RenderedBillboard(billboard, new Dimension(900, 500)));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public void createUIComponents() {
        colourChooser = new ColourChooser();
    }
}