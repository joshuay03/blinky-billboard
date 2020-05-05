package BillboardSupport;

import BillboardViewer.Viewer;
import org.junit.platform.commons.util.BlacklistedExceptions;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.tools.Tool;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Console;
import java.net.URL;

public class RenderedBillboard extends JPanel {

    private static final String defaultBillboardFont = "Trebuchet";

    private static JLabel imageContainer = new JLabel();
    private static JTextPane messageContainer = new JTextPane(), informationContainer = new JTextPane();

    /*
    private ImageIcon getScaledImage (ImageIcon src, float targetProportion){
        // Determine larger screen dimension
        float targetLargerDimension;

        if (Toolkit.getDefaultToolkit().getScreenSize().width > Toolkit.getDefaultToolkit().getScreenSize().height){
            // Vertical orientation screen
            targetLargerDimension = Toolkit.getDefaultToolkit().getScreenSize().width * targetProportion;
        } else {
            // Vertical orientation screen OR square screen (lol
            targetLargerDimension = Toolkit.getDefaultToolkit().getScreenSize().height * targetProportion;
        }

        // Determine whether x or y size of image is larger, or whether square
        float scalingFactor; // Target size is half of the largest screen dimension
        if(src.getIconWidth() > src.getIconHeight()){ // Wide image

            scalingFactor = (float) targetLargerDimension / (float)src.getIconWidth();

        } else { // Square image OR narrow image, use height as larger dimension

            scalingFactor = (float)targetLargerDimension / (float)src.getIconHeight();
        }

        Image srcImg = src.getImage();
        Image newImg = srcImg.getScaledInstance((int)(src.getIconWidth() * scalingFactor), (int)(src.getIconHeight() * scalingFactor), Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }*/

    private ImageIcon getScaledImage (ImageIcon src, int maxWidth, int maxHeight){

        // Determine which side to bound the scaling on
        float scalingFactor = 0.0f;
        if(maxHeight <= maxWidth){
             scalingFactor = maxHeight / src.getIconHeight(); // Height dependent scaling - must calc tallest we can make the image
        } else if(maxWidth < maxHeight) {
            scalingFactor = maxWidth / src.getIconWidth(); // Width depending scaling - can't go larger than a particular width
        }

        Image srcImg = src.getImage();
        Image newImg = srcImg.getScaledInstance((int)(src.getIconWidth() * scalingFactor), (int)(src.getIconHeight() * scalingFactor), Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    public RenderedBillboard(Billboard board, Dimension d) {
        super();
        this.setBackground(Color.WHITE);
        this.setSize(d);
        this.setLayout(new GridBagLayout());

        //-----------------------------------
        // Initial Grid set up
        // ----------------------------------
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.CENTER;

        //Handle text centering etc...
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        messageContainer.setParagraphAttributes(center, false);
        informationContainer.setParagraphAttributes(center, false);

        // ----------------------------------
        // GET CONTENT
        // ----------------------------------
        messageContainer.setText(board.getMessage());
        informationContainer.setText(board.getInformation());

        messageContainer.setFocusable(false);
        informationContainer.setFocusable(false);
        imageContainer.setFocusable(false);

        // ----------------------------------
        // HANDLE RENDERING PROPORTIONS SETUP
        // ----------------------------------
        // ONLY message present...
        if (board.getBillboardImage() == null && board.getInformation() == null && board.getMessage() != null) {

            // Message should be displayed as large as possible while still (1) fully on screen, and (2) with no line breaks

            constraints.gridx = 0;
            constraints.gridy = 1;

            messageContainer.setFont(new Font(defaultBillboardFont, Font.BOLD, d.width / board.getMessage().length()));
            System.out.println(messageContainer.getHeight());
            this.add(messageContainer, constraints);
        }

        // ONLY picture present
        if (board.getBillboardImage() != null && board.getInformation() == null && board.getMessage() == null) {

            constraints.gridx = 0;
            constraints.gridy = 1;

            imageContainer.setIcon(getScaledImage(board.getBillboardImage(),
                                                (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 1/2),
                                                (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 1/2)));
            this.add(imageContainer, constraints);

        }

        // ONLY information present
        if (board.getBillboardImage() == null && board.getInformation() != null && board.getMessage() == null) {
            // Text should be displayed in centre, and filling only 75% screen width, 50% screen height

            constraints.gridx = 0;
            constraints.gridy = 0;

            int     xSize = (int)((Toolkit.getDefaultToolkit().getScreenSize().getWidth() * (0.75))), // Only use 75% of the height
                    ySize = (int)((Toolkit.getDefaultToolkit().getScreenSize().getHeight() * (0.50))); // Only use 50% of height

            // Start with a modest size and then increase the font size until preferredsize exceeds either dimension
            informationContainer.setMaximumSize(new Dimension(xSize, ySize));
            informationContainer.setMinimumSize(new Dimension(xSize, ySize));

           //TODO - Dynamic calculation of font size to fill max area
            informationContainer.setFont(new Font(defaultBillboardFont, Font.BOLD, 80));

            constraints.anchor = GridBagConstraints.CENTER;

            this.add(informationContainer, constraints);
        }

        // Message AND picture present
        if (board.getBillboardImage() != null && board.getInformation() == null && board.getMessage() != null) {


            // Message on top 1/3 of screen, filling width
            constraints.gridy = 0;
            messageContainer.setFont(new Font(defaultBillboardFont, Font.BOLD, 60)); //TODO - Dynamic font size calculation
            constraints.anchor = GridBagConstraints.CENTER;
            this.add(messageContainer, constraints);

            // Picture filling bottom 2/3
            imageContainer.setIcon(getScaledImage(board.getBillboardImage(),
                    (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                    (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 1/3)));
            constraints.gridy = 1;
            constraints.gridheight = 2;
            this.add(imageContainer, constraints);


        }

        // Message AND information present
        if (board.getBillboardImage() == null && board.getInformation() != null && board.getMessage() != null) {
            messageContainer.setFont(new Font(defaultBillboardFont, Font.BOLD, 60));
            messageContainer.setMinimumSize(new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth())
                                                        , (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2)));
            constraints.gridy = 0;
            constraints.gridx = 0;
            // TODO - Message not correctly centering within view

            this.add(messageContainer, constraints);

            informationContainer.setFont(new Font(defaultBillboardFont, Font.BOLD, 40));
            informationContainer.setMinimumSize(new Dimension((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth())
                    , (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2)));
            constraints.gridy = 1;
            constraints.gridx = 0;

            this.add(informationContainer, constraints);
        }

        // Picture AND information present
        if (board.getBillboardImage() != null && board.getInformation() != null && board.getMessage() == null) {

            // Picture in top 2/3 of screen, centered
            imageContainer.setIcon(getScaledImage(board.getBillboardImage(),
                                                (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                                                (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 1/3)));
            constraints.gridy = 0;
            constraints.gridheight = 2;
            this.add(imageContainer, constraints);

            // Information in bottom 1/3 of screen, centered, no more than 75% of screen width
            constraints.gridy = 2;
            informationContainer.setFont(new Font(defaultBillboardFont, Font.BOLD, 60)); //TODO - Dynamic font size calculation
            informationContainer.setMaximumSize(new Dimension(  (int)(Toolkit.getDefaultToolkit().getScreenSize().width *.75),
                                                                (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2)));
            constraints.anchor = GridBagConstraints.CENTER;
            this.add(informationContainer, constraints);
        }

        // Picture AND image AND information present
        if (board.getBillboardImage() != null && board.getInformation() != null && board.getMessage() != null) {

            // Message in top 1/3 of screen, centered, no more than 75% of screen width
            constraints.gridy = 0;
            messageContainer.setFont(new Font(defaultBillboardFont, Font.BOLD, 30)); //TODO - Dynamic font size calculation
            this.add(messageContainer, constraints);

            // TODO - FIXME - Image rendering on LHS for some reason...
            // Picture in middle 1/3 of screen, centered
            imageContainer.setIcon(getScaledImage(board.getBillboardImage(),
                    (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()),
                    (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 1/3)));
            constraints.gridy = 1;
            this.add(imageContainer, constraints);

            // Information in bottom 1/3 of screen, centered, no more than 75% of screen width
            constraints.gridy = 2;
            informationContainer.setFont(new Font(defaultBillboardFont, Font.BOLD, 15)); //TODO - Dynamic font size calculation
            this.add(informationContainer, constraints);
        }

        // Un-renderable billboard - no data
        if (board.getBillboardImage() == null && board.getInformation() == null && board.getMessage() == null) {
            // TODO - Code to handle dud billboards appropriately
            //throw new Exception("board billboard does not contain any data to render. Please check it was properly instantitated");
        }

    }

}

