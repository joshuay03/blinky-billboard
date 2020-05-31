package ControlPanel;

import BillboardSupport.Billboard;
import BillboardSupport.RenderedBillboard;
import Client.ClientConnector;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * A frame which allows a preview of a billboard's contents.
 */
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

    /**
     * Scales a given image
     * @param src The image source
     * @param maxWidth The max width of the image
     * @param maxHeight The max height of the image
     * @return The new scaled image
     */
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

    /**
     * Takes in a billboard and displays its contents onto the page.
     * @param billboard
     */
    public PreviewBillboardContents(Billboard billboard) {
        if(billboard.getBillboardName() != null) {
            billboardNameLabel.setText(billboard.getBillboardName());
        }
        if(billboard.getMessage() != null) {
            messageLabel.setText(billboard.getMessage());
        }
        if(billboard.getMessageColour() != null) {
            messageColorLabel.setText(billboard.getMessageColour().toString().replace("java.awt.Color", ""));
        }
        if(billboard.getImageURL() != null) {
            picUrlLabel.setText(billboard.getImageURL().toString());
        }
        if(billboard.getInformation() != null) {
            informationTextArea.setText(billboard.getInformation());
            informationTextArea.setWrapStyleWord(true);
            informationTextArea.setLineWrap(true);
        }
        if( billboard.getInformationColour() != null ) {
            infoColorLabel.setText(billboard.getInformationColour().toString().replace("java.awt.Color", ""));
        }
        if(billboard.getBackgroundColour() != null) {
           backgroundLabel.setText(billboard.getBackgroundColour().toString().replace("java.awt.Color", ""));
        }
        if(billboard.getImageData() != null) {
            picDataLabel.setIcon(getScaledImage(RenderedBillboard.getImageIconFromBase64(billboard.getImageData()), 200, 100));
        }
    }
}
