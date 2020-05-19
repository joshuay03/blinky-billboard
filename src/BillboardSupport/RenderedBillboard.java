package BillboardSupport;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class RenderedBillboard extends JPanel {

    private static final String defaultBillboardFont = "Trebuchet";
    private static final double PADDING_PERCENTAGE = 0.90;
    private static final double FONT_SIZE_INCREASE_RATE = 2;

    static Canvas headlessCanvas = new Canvas();

    private JLabel imageContainer;
    private JTextPane messageContainer, informationContainer;

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
             scalingFactor = (float)maxHeight / src.getIconHeight(); // Height dependent scaling - must calc tallest we can make the image
        } else if(maxWidth < maxHeight) {
            scalingFactor = (float)maxWidth / src.getIconWidth(); // Width depending scaling - can't go larger than a particular width
        }

        Image srcImg = src.getImage();
        Image newImg = srcImg.getScaledInstance((int)(src.getIconWidth() * scalingFactor), (int)(src.getIconHeight() * scalingFactor), Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    /**
     *
     * @param board The billboard that you would like to render
     * @param renderDimensions The dimensions of the area you want to render the billboard in
     */
    public RenderedBillboard(Billboard board, Dimension renderDimensions) {
        super();
        this.setBackground(Color.WHITE);
        this.setSize(renderDimensions);
        this.setLayout(new GridBagLayout());

        //Initialise member objects
        this.messageContainer = new JTextPane();
        this.informationContainer = new JTextPane();
        this.imageContainer = new JLabel();

        //-----------------------------------
        // Initial Grid set up
        // ----------------------------------
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;

        // ----------------------------
        // HANDLE COMMON FORMATTING
        // ----------------------------
        SimpleAttributeSet paragraphAttributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(paragraphAttributes, StyleConstants.ALIGN_CENTER);
        StyleConstants.setSpaceAbove(paragraphAttributes, 0);

        informationContainer.setParagraphAttributes(paragraphAttributes, false);
        messageContainer.setParagraphAttributes(paragraphAttributes, false);

        messageContainer.setForeground(board.getMessageColour());
        informationContainer.setForeground(board.getInformationColour());
        this.setBackground(board.getBackgroundColour());

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
            Font boardFont = getScaledFontForArea((int) renderDimensions.getWidth(), (int) renderDimensions.getHeight(), board.getMessage(), false);

            messageContainer.setFont(boardFont);
            messageContainer.setMaximumSize(renderDimensions.getSize());

            // Font added to centre automatically, due to gridbag constraint setup - anchor to centre
            this.add(messageContainer, constraints);
            this.repaint();
        }

        // ONLY picture present
        if (board.getBillboardImage() != null && board.getInformation() == null && board.getMessage() == null) {

            imageContainer.setIcon(getScaledImage(board.getBillboardImage(), (int) renderDimensions.getWidth(), (int) (renderDimensions.getHeight())));
            this.add(imageContainer, constraints);

        }

        // ONLY information present
        if (board.getBillboardImage() == null && board.getInformation() != null && board.getMessage() == null) {

            // Pad the top
            constraints.gridy = 0;
            this.add(new JPanel(),constraints);

            // Text should be displayed filling only 75% screen width, 50% screen height
            int     textWidthLimit = (int) (renderDimensions.getWidth() * 0.75), // Only use 75% of the height
                    textHeightLimit = (int) (renderDimensions.getHeight() * 0.50); // Only use 50% of height

            Font boardFont = getScaledFontForArea(textWidthLimit, textHeightLimit, board.getInformation(), true);

            informationContainer.setMaximumSize(new Dimension(textWidthLimit, textHeightLimit));

            informationContainer.setFont(boardFont);

            this.add(informationContainer, constraints);

            // Pad the bottom
            constraints.gridy = 2;
            this.add(new JPanel(), constraints);


        }

        // Message AND picture present
        if (board.getBillboardImage() != null && board.getInformation() == null && board.getMessage() != null) {

            constraints.gridy = 0;
            // Pad the top
            this.add(new JPanel());

            // Message on top 1/3 of screen, filling width
            constraints.gridy = 1;

            int     widthLimit = (int) (renderDimensions.getWidth()),
                    heightLimit = (int) (renderDimensions.getHeight() / 3.0);

            //FIXME - displays fine, but still manages to wrap on occasion
            messageContainer.setFont(getScaledFontForArea(widthLimit, heightLimit, board.getMessage(), false));
            messageContainer.setMaximumSize(new Dimension(widthLimit, heightLimit));

            this.add(messageContainer, constraints);

            // Picture filling bottom 2/3
            imageContainer.setIcon(getScaledImage(board.getBillboardImage(),
                    (int) (renderDimensions.getWidth()),
                    (int) (renderDimensions.getHeight() / 3.0)));

            constraints.gridy = 2;
            constraints.gridheight = 2;
            this.add(imageContainer, constraints);

            constraints.gridy = 5;
            this.add(imageContainer, constraints);

        }

        // Message AND information present
        if (board.getBillboardImage() == null && board.getInformation() != null && board.getMessage() != null) {

            // Pad the top
            constraints.gridy = 0;
            this.add(new JPanel(), constraints);

            // TODO - Vertical Alignment - objects are forcing max size, so layout manager can't do anything
            // 50% of height for each of the message and information - use the same values
            int     widthLimit = (int) (renderDimensions.getWidth()),
                    heightLimit = (int) (renderDimensions.getHeight() * 0.5);

            // SET UP MESSAGE CONTAINER

            messageContainer.setFont(getScaledFontForArea(widthLimit, heightLimit, board.getMessage(), false));

            messageContainer.setPreferredSize(new Dimension(widthLimit, heightLimit));
            constraints.gridy = 1;

            this.add(messageContainer, constraints);

            // Pad the middle
            constraints.gridy = 2;
            this.add(new JPanel());

            // SET UP INFORMATION CONTAINER

            informationContainer.setFont(getScaledFontForArea(widthLimit, heightLimit, board.getInformation(), true));
            informationContainer.setPreferredSize(new Dimension(widthLimit, heightLimit));
            constraints.gridy = 3;

            this.add(informationContainer, constraints);

            constraints.gridy = 4;
            this.add(new JPanel(), constraints);
        }

        // Picture AND information present
        if (board.getBillboardImage() != null && board.getInformation() != null && board.getMessage() == null) {

            // Pad the top
            constraints.gridy = 0;
            this.add(new JPanel());

            // Picture in top 2/3 of screen, centered
            imageContainer.setIcon(getScaledImage(board.getBillboardImage(),
                    (int) (renderDimensions.getWidth()),
                    (int) (renderDimensions.getHeight() / 3.0)));
            imageContainer.setMaximumSize(new Dimension((int) (renderDimensions.getWidth()), (int) (renderDimensions.getHeight() / 3.0)));

            constraints.gridy = 1;
            constraints.gridheight = 2;
            this.add(imageContainer, constraints);

            // Information in bottom 1/3 of screen, centered, no more than 75% of screen width
            constraints.gridy = 2;

            int     informationWidthLimit = (int) (renderDimensions.getWidth() * 0.75),
                    informationHeightLimit = (int) (renderDimensions.getHeight() / 3.0);

            informationContainer.setFont(getScaledFontForArea(informationWidthLimit, informationHeightLimit, board.getInformation(), true)); //TODO - Dynamic font size calculation
            informationContainer.setPreferredSize(new Dimension(informationWidthLimit, informationHeightLimit));
            informationContainer.setBackground(Color.green);

            this.add(informationContainer, constraints);

            constraints.gridy = 5;
            this.add(new JPanel());
        }

        // Picture AND image AND information present
        if (board.getBillboardImage() != null && board.getInformation() != null && board.getMessage() != null) {
            // Pad the top
            constraints.gridy = 0;
            this.add(new JPanel());

            // Both message and information must be in 1/3 of screen, centered, no more than 75% of screen width
            // So use the same values
            int     textWidthLimit = (int) (renderDimensions.getWidth() * 0.75),
                    textHeightLimit = (int) (renderDimensions.getHeight() / 3.0);

            // Message in top 1/3 of screen, centered, no more than 75% of screen width

            messageContainer.setFont(getScaledFontForArea(textWidthLimit, textHeightLimit, board.getMessage(), false));
            messageContainer.setMaximumSize(new Dimension(textWidthLimit, textHeightLimit));

            messageContainer.setBackground(Color.GRAY);

            constraints.gridy = 1;

            this.add(messageContainer, constraints);

            // Pad the gap
            constraints.gridy = 2;
            this.add(new JPanel());

            // Picture in middle 1/3 of screen, centered
            int     imageWidthLimit = (int) (renderDimensions.getWidth()),
                    imageHeightLimit = (int) (renderDimensions.getHeight()/3.0);

            ImageIcon scaledBillboardImage = getScaledImage(board.getBillboardImage(), imageWidthLimit - 1, imageHeightLimit - 1);
            imageContainer.setIcon(scaledBillboardImage);

            imageContainer.setMaximumSize(new Dimension(imageWidthLimit, imageHeightLimit));
            constraints.gridy = 3;

            this.add(imageContainer, constraints);

            // Pad the gap
            constraints.gridy = 4;
            this.add(new JPanel());

            //Information in bottom 1/3 of screen, centered, no more than 75% of screen width

            informationContainer.setFont(getScaledFontForArea(textWidthLimit, textHeightLimit, board.getInformation(), true));
            informationContainer.setMaximumSize(new Dimension(textWidthLimit, textHeightLimit));

            constraints.gridy = 5;
            this.add(informationContainer, constraints);
        }

        // Un-renderable billboard - no data
        if (board.getBillboardImage() == null && board.getInformation() == null && board.getMessage() == null) {
            // TODO - Code to handle dud billboards appropriately
            //throw new Exception("board billboard does not contain any data to render. Please check it was properly instantitated");
        }
    }

    Font getScaledFontForArea(int widthLimit, int heightLimit, String stringToRender, boolean allowWrap){
        Font boardFont = new Font(defaultBillboardFont, Font.BOLD, 10);

        int renderedStringWidth, renderedStringHeight;

            while (true) {
                boardFont = boardFont.deriveFont((float) (boardFont.getSize() + FONT_SIZE_INCREASE_RATE));

                FontMetrics fontMetrics = headlessCanvas.getFontMetrics(boardFont);

                // Calculate the number of rows required to render the text in a given width (text width / width of screen)
                int requiredRowsToRender = (int) Math.ceil((float)fontMetrics.stringWidth(stringToRender) / (float)widthLimit);

                //Determine width and height of the string
                renderedStringHeight = (fontMetrics.getHeight() + fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent()) * requiredRowsToRender;
                renderedStringWidth = fontMetrics.stringWidth(stringToRender);

                //Wrapped scaling
                if(allowWrap) {
                    // If the information, wrapped, is the right height for the screen, we should be done
                    if (renderedStringHeight >= heightLimit * PADDING_PERCENTAGE) break;

                    // If we've gone too wide for the screen...
                    if (renderedStringWidth / requiredRowsToRender >= widthLimit) {
                        // Check whether another line would cause us to blow out of our boundaries
                        if((requiredRowsToRender + 1) * fontMetrics.getHeight() <= heightLimit) {
                            // .... If not, add another row
                            requiredRowsToRender++;
                        }
                        // ... If it is, that's the limit
                        else {
                            break;
                        }
                    }
                }

                // Unwrapped scaling - one line only
                else {
                    if (renderedStringWidth >= widthLimit * PADDING_PERCENTAGE) break;
                }
            }

        return boardFont;
    }
}

