package ControlPanel;

import javax.swing.*;

public class CreateBillboards {
    protected JPanel createBillboardsPanel;
    protected JPanel optionPanel;
    protected JPanel createPanel;
    protected JButton viewBillboardButton;
    protected JTextArea textArea1;
    protected JFormattedTextField formattedTextField1;
    protected JTextArea textArea2;
    protected JLabel messageLabel;
    protected JLabel pictureLabel;
    protected JLabel informationLabel;
    protected JLabel backgroundLabel;
    protected JPanel colourPanel;
    protected ColourChooser colourChooser;

    public CreateBillboards(JFrame frame) {
    }

    public void createUIComponents() {
        colourChooser = new ColourChooser();
    }
}