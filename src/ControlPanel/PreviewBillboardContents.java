package ControlPanel;

import BillboardSupport.Billboard;
import BillboardSupport.RenderedBillboard;

import javax.swing.*;
import java.awt.*;

public class PreviewBillboardContents {
    protected JPanel previewBillboardContentsPanel;
    private JLabel pictureLabel;
    private JLabel billboardNameLabel;
    private JTextArea informationTextArea;
    private JPanel messageColourPanel;
    private JPanel informationColourPanel;
    private JPanel backgroundColourPanel;
    private JTextArea messageTextArea;

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
        if (billboard.getBillboardName() != null) {
            billboardNameLabel.setText(billboard.getBillboardName());
        }
        if (billboard.getMessage() != null) {
            messageTextArea.setText(billboard.getMessage());
        }
        if (billboard.getMessageColour() != null) {
            messageColourPanel.setBackground(billboard.getMessageColour());
        }
        if (billboard.getInformation() != null) {
            informationTextArea.setText(billboard.getInformation());
            informationTextArea.setWrapStyleWord(true);
            informationTextArea.setLineWrap(true);
        }
        if (billboard.getInformationColour() != null ) {
            informationColourPanel.setBackground(billboard.getInformationColour());
        }
        if (billboard.getBackgroundColour() != null) {
            backgroundColourPanel.setBackground(billboard.getBackgroundColour());
        }
        if (billboard.getImageData() != null) {
            pictureLabel.setIcon(getScaledImage(RenderedBillboard.getImageIconFromBase64(billboard.getImageData()), 200, 100));
        }
        else if (billboard.getImageURL() != null) {
            pictureLabel.setIcon(getScaledImage(RenderedBillboard.getImageIconFromURL(billboard.getImageURL()), 200, 100));
        }
    }
}
