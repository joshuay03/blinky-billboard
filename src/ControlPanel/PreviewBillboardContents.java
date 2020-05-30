package ControlPanel;

import BillboardSupport.Billboard;
import BillboardSupport.RenderedBillboard;
import Client.ClientConnector;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PreviewBillboardContents {
    protected JPanel previewBillboardContentsPanel;
    private JLabel infoColorLabel;
    private JLabel backgroundLabel;
    private JLabel infoLabel;
    private JLabel picUrlLabel;
    private JLabel picDataLabel;
    private JLabel messageColorLabel;
    private JLabel messageLabel;
    private JLabel billboardNameLabel;
    private JTextArea informationTextArea;
    private Billboard billboard;

    private ImageIcon getScaledImage(ImageIcon src, int maxWidth, int maxHeight) {

        // Determine which side to bound the scaling on
        float scalingFactor = 0.0f;
        if (maxHeight <= maxWidth) {
            scalingFactor = (float) maxHeight / src.getIconHeight(); // Height dependent scaling - must calc tallest we can make the image
        } else if (maxWidth < maxHeight) {
            scalingFactor = (float) maxWidth / src.getIconWidth(); // Width depending scaling - can't go larger than a particular width
        }

        Image srcImg = src.getImage();
        Image newImg = srcImg.getScaledInstance((int) (src.getIconWidth() * scalingFactor), (int) (src.getIconHeight() * scalingFactor), Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }


    public PreviewBillboardContents(Billboard billboard) {
        Color infoColor = billboard.getInformationColour();
        if( infoColor != null ) {
            infoColorLabel.setText(infoColor.toString().replace("java.awt.Color", ""));
        }
         Color background = billboard.getBackgroundColour();
        if(background != null) {
           backgroundLabel.setText(background.toString().replace("java.awt.Color", ""));
        }
        String info = billboard.getInformation();
        if(info != null) {
            informationTextArea.setText(info);
            informationTextArea.setWrapStyleWord(true);
            informationTextArea.setLineWrap(true);
        }
        URL picUrl = billboard.getImageURL();
        if(picUrl != null) {
            picUrlLabel.setText(picUrl.toString());
        }
        String picData = billboard.getImageData();
        if(picData != null) {
            picDataLabel.setIcon(getScaledImage(RenderedBillboard.getImageIconFromBase64(picData), 200, 100));
        }
        Color messageColor = billboard.getMessageColour();
        if(messageColor != null) {
            messageColorLabel.setText(messageColor.toString().replace("java.awt.Color", ""));
        }
        String message = billboard.getMessage();
        if(message != null) {
            messageLabel.setText(message);
        }
        String billboardName = billboard.getBillboardName();
        if(billboardName != null) {
            billboardNameLabel.setText(billboardName);
        }
    }
}
