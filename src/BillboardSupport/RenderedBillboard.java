package BillboardSupport;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class RenderedBillboard extends JPanel {

    private static final String defaultBillboardFont = "Trebuchet";
    private static final double PADDING_PERCENTAGE = 0.90;
    private static final double FONT_SIZE_INCREASE_RATE = 2;

    static Canvas headlessCanvas = new Canvas();

    private JLabel imageContainer;
    private JTextPane messageContainer, informationContainer;

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
        this.setSize(renderDimensions);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        //Initialise member objects
        this.messageContainer = new JTextPane();
        this.informationContainer = new JTextPane();
        this.imageContainer= new JLabel();

        // ----------------------------
        // HANDLE COMMON FORMATTING
        // ----------------------------
       // Formatting and colour handling for the message container
        SimpleAttributeSet messageAttributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(messageAttributes, StyleConstants.ALIGN_CENTER);
        StyleConstants.setSpaceAbove(messageAttributes, 0);
        StyleConstants.setForeground(messageAttributes, board.getMessageColour());
//        messageContainer.setParagraphAttributes(messageAttributes, false);
        messageContainer.setOpaque(false);

        // Formatting and colour handling for the information container
        SimpleAttributeSet informationAttributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(informationAttributes, StyleConstants.ALIGN_CENTER);
        StyleConstants.setSpaceAbove(informationAttributes, 0);
        StyleConstants.setForeground(informationAttributes, board.getMessageColour());

//        informationContainer.setParagraphAttributes(informationAttributes, false);
        informationContainer.setOpaque(false);

        // Formatting for the image container
        imageContainer.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        imageContainer.setHorizontalAlignment(JLabel.CENTER);

        // Paint the plain default background colour
        this.setBackground(board.getBackgroundColour());

        // ----------------------------------
        // GET CONTENT FROM THE BILLBOARD OBJECT
        // ----------------------------------
        messageContainer.setText(board.getMessage());
        informationContainer.setText(board.getInformation());

        // Prevent the objects from being able to take focus
        messageContainer.setFocusable(false);
        informationContainer.setFocusable(false);
        imageContainer.setFocusable(false);

        // ----------------------------------
        // HANDLE RENDERING PROPORTIONS SETUP
        // ----------------------------------
        // ONLY message present...
        if (board.getBillboardImage() == null && board.getInformation() == null && board.getMessage() != null) {

            int     widthLimit = (int) (renderDimensions.getWidth()),
                    heightLimit = (int) (renderDimensions.getHeight());

            // Message should be displayed as large as possible while still (1) fully on screen, and (2) with no line breaks
            Font boardFont = getScaledFontForArea(widthLimit, heightLimit, board.getMessage(), false);

            messageContainer.setFont(boardFont);
            messageContainer.setMaximumSize(new Dimension(widthLimit, heightLimit));

            addComponent(Box.createGlue());
            addComponent(messageContainer);
            addComponent(Box.createGlue());
        }

        // ONLY picture present
        if (board.getBillboardImage() != null && board.getInformation() == null && board.getMessage() == null) {

            int     widthLimit = (int) (renderDimensions.getWidth() * 0.5),
                    heightLimit = (int) (renderDimensions.getHeight() * 0.5 );

            imageContainer.setIcon(getScaledImage(board.getBillboardImage(), widthLimit, heightLimit));
            imageContainer.setAlignmentX(JLabel.CENTER_ALIGNMENT);

            addComponent(Box.createGlue());
            addComponent(imageContainer);
            addComponent(Box.createGlue());

        }

        // ONLY information present
        if (board.getBillboardImage() == null && board.getInformation() != null && board.getMessage() == null) {

            // Text should be displayed filling only 75% screen width, 50% screen height
            int     textWidthLimit = (int) (renderDimensions.getWidth() * 0.75), // Only use 75% of the height
                    textHeightLimit = (int) (renderDimensions.getHeight() * 0.50); // Only use 50% of height

            Font boardFont = getScaledFontForArea(textWidthLimit, textHeightLimit, board.getInformation(), true);

            informationContainer.setFont(boardFont);
            informationContainer.setMaximumSize(new Dimension(textWidthLimit, textHeightLimit));


            addComponent(Box.createGlue());
            addComponent(informationContainer);
            addComponent(Box.createGlue());

        }

        // Message AND picture present
        if (board.getBillboardImage() != null && board.getInformation() == null && board.getMessage() != null) {

            // Message on top 1/3 of screen, filling width
            int     messageWidthLimit = (int) (renderDimensions.getWidth()),
                    messageHeightLimit = (int) (renderDimensions.getHeight() / 3.0);

            //FIXME - displays fine, but still manages to wrap on occasion
            messageContainer.setFont(getScaledFontForArea(messageWidthLimit, messageHeightLimit, board.getMessage(), false));
            messageContainer.setMaximumSize(new Dimension(messageWidthLimit, messageHeightLimit));

            // Picture filling bottom 2/3
            imageContainer.setIcon(getScaledImage(board.getBillboardImage(),
                    (int) (renderDimensions.getWidth()),
                    (int) (renderDimensions.getHeight() / 3.0)));

            addComponent(Box.createGlue());
            addComponent(messageContainer);
            addComponent(Box.createGlue());
            addComponent(imageContainer);
            addComponent(Box.createGlue());

        }

        // Message AND information present
        if (board.getBillboardImage() == null && board.getInformation() != null && board.getMessage() != null) {

            // 50% of height for each of the message and information - use the same values
            int     widthLimit = (int) (renderDimensions.getWidth()),
                    heightLimit = (int) (renderDimensions.getHeight() * 0.5);

            messageContainer.setFont(getScaledFontForArea(widthLimit, heightLimit, board.getMessage(), false));
            messageContainer.setMaximumSize(new Dimension(widthLimit, heightLimit));


            informationContainer.setFont(getScaledFontForArea(widthLimit, heightLimit, board.getInformation(), true));
            informationContainer.setMaximumSize(new Dimension(widthLimit, heightLimit));

            addComponent(Box.createGlue());
            addComponent(messageContainer);
            addComponent(Box.createGlue());
            addComponent(informationContainer);
            addComponent(Box.createGlue());

        }

        // Picture AND information present
        if (board.getBillboardImage() != null && board.getInformation() != null && board.getMessage() == null) {

            // Picture in top 2/3 of screen, centered
            int imageWidthLimit = (int) (renderDimensions.getWidth()),
                    imageHeightLimit = (int) (renderDimensions.getHeight() / 3.0);

            imageContainer.setIcon(getScaledImage(board.getBillboardImage(), imageWidthLimit, imageHeightLimit));

            imageContainer.setMaximumSize(new Dimension(imageWidthLimit, imageHeightLimit));

            // Information in bottom 1/3 of screen, centered, no more than 75% of screen width

            int     informationWidthLimit = (int) (renderDimensions.getWidth() * 0.75),
                    informationHeightLimit = (int) (renderDimensions.getHeight() / 3.0);

            informationContainer.setFont(getScaledFontForArea(informationWidthLimit, informationHeightLimit, board.getInformation(), true)); //TODO - Dynamic font size calculation
            informationContainer.setMaximumSize(new Dimension(informationWidthLimit, informationHeightLimit));

            addComponent(Box.createGlue());
            addComponent(imageContainer);
            addComponent(Box.createGlue());
            addComponent(informationContainer);
            addComponent(Box.createGlue());

        }

        // Picture AND image AND information present
        if (board.getBillboardImage() != null && board.getInformation() != null && board.getMessage() != null) {

            // Both message and information must be in 1/3 of screen, centered, no more than 75% of screen width
            // So use the same values
            int     textWidthLimit = (int) (renderDimensions.getWidth() * 0.75),
                    textHeightLimit = (int) (renderDimensions.getHeight() / 3.0);

            messageContainer.setFont(getScaledFontForArea(textWidthLimit, textHeightLimit, board.getMessage(), false));
            messageContainer.setMaximumSize(new Dimension(textWidthLimit, textHeightLimit));

            // Picture in middle 1/3 of screen, centered
            int     imageWidthLimit = (int) (renderDimensions.getWidth()),
                    imageHeightLimit = (int) (renderDimensions.getHeight()/3.0);

            imageContainer.setIcon(getScaledImage(board.getBillboardImage(), imageWidthLimit, imageHeightLimit));
            imageContainer.setMaximumSize(new Dimension(imageWidthLimit,imageHeightLimit));
            //Information in bottom 1/3 of screen, centered, no more than 75% of screen width

            informationContainer.setFont(getScaledFontForArea(textWidthLimit, textHeightLimit, board.getInformation(), true));
            informationContainer.setMaximumSize(new Dimension(textWidthLimit, textHeightLimit));

            addComponent(Box.createGlue());
            addComponent(messageContainer);
            addComponent(Box.createGlue());
            addComponent(imageContainer);
            addComponent(Box.createGlue());
            addComponent(informationContainer);
            addComponent(Box.createGlue());
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
                    if (renderedStringWidth >= widthLimit * PADDING_PERCENTAGE || renderedStringHeight >= heightLimit * PADDING_PERCENTAGE) break;
                }
            }

        return boardFont;
    }

    void addComponent(Component c){
        this.add(c);
    }


}

