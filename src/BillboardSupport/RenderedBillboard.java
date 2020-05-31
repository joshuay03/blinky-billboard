package BillboardSupport;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

public class RenderedBillboard extends JPanel {

    private static final String DEFAULT_BILLBOARD_FONT = "Trebuchet";
    private static final double PADDING_PERCENTAGE = 0.90;
    private static final double FONT_SIZE_INCREASE_RATE = 2;
    private static final Color DEFAULT_COLOUR = Color.WHITE;

    static Canvas headlessCanvas = new Canvas();

    private JLabel imageContainer;
    private JTextPane messageContainer, informationContainer;

    /** Returns a RenderedBillboard Object for insertion into a JFrame
     * @param board            The billboard that you would like to render
     * @param renderDimensions The dimensions of the area you want to render the billboard in
     */
    public RenderedBillboard(Billboard board, Dimension renderDimensions) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));



        //Initialise member objects
        this.messageContainer = new JTextPane();
        this.informationContainer = new JTextPane();
        this.imageContainer = new JLabel();

        // ----------------------------
        // HANDLE COMMON FORMATTING
        // ----------------------------
        // Formatting and colour handling for the message container
        /*SimpleAttributeSet messageAttributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(messageAttributes, StyleConstants.ALIGN_CENTER);
        StyleConstants.setSpaceAbove(messageAttributes, 0);
        StyleConstants.setForeground(messageAttributes, board.getMessageColour());
        messageContainer.setParagraphAttributes(messageAttributes, false);
        messageContainer.setOpaque(false);*/

        // Formatting and colour handling for the information container
        SimpleAttributeSet textAttributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(textAttributes, StyleConstants.ALIGN_CENTER);
        StyleConstants.setSpaceAbove(textAttributes, 0);
        StyleConstants.setBackground(textAttributes, new Color(0,0,0,0.0f));

        StyleConstants.setForeground(textAttributes, board.getInformationColour());
        informationContainer.setParagraphAttributes(textAttributes, false);
        informationContainer.setOpaque(false);

        StyleConstants.setForeground(textAttributes, board.getMessageColour());
        messageContainer.setOpaque(false);
        messageContainer.setParagraphAttributes(textAttributes, false);

        // Formatting for the image container
        imageContainer.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        imageContainer.setHorizontalAlignment(JLabel.CENTER);

        // Paint the background colour, if any
        if (board.getBackgroundColour() != null) {
            this.setBackground(board.getBackgroundColour());
        }
        // Otherwise, use a default
        else {
            this.setBackground(DEFAULT_COLOUR);
        }

        // ----------------------------------
        // GET CONTENT FROM THE BILLBOARD OBJECT
        // ----------------------------------
        messageContainer.setText(board.getMessage());
        informationContainer.setText(board.getInformation());

        if (board.getImageURL() != null) {
            imageContainer.setIcon(getImageIconFromURL(board.getImageURL()));
        } else if (board.getImageData() != null) {
            imageContainer.setIcon(getImageIconFromBase64(board.getImageData()));
        }

        // Prevent the objects from being able to take focus
        messageContainer.setFocusable(false);
        informationContainer.setFocusable(false);
        imageContainer.setFocusable(false);

        boolean hasImage = board.getImageURL() != null ^ board.getImageData() != null;

        boolean hasInformation = false;
        if(board.getInformation() != null) hasInformation = board.getInformation().length() > 0;

        boolean hasMessage = false;
        if(board.getMessage() != null) hasMessage = board.getMessage().length() > 0;

        // ----------------------------------
        // HANDLE RENDERING PROPORTIONS SETUP
        // ----------------------------------
        // ONLY message present...
        if (hasImage == false && hasInformation == false && hasMessage == true) {

            int widthLimit = (int) (renderDimensions.getWidth()),
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
        if (hasImage == true && hasInformation == false && hasMessage == false) {

            int widthLimit = (int) (renderDimensions.getWidth() * 0.5),
                    heightLimit = (int) (renderDimensions.getHeight() * 0.5);

            imageContainer.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            imageContainer.setIcon(getScaledImage((ImageIcon) imageContainer.getIcon(), widthLimit, heightLimit));

            addComponent(Box.createGlue());
            addComponent(imageContainer);
            addComponent(Box.createGlue());

        }

        // ONLY information present
        if (hasImage == false && hasInformation == true && hasMessage == false) {

            // Text should be displayed filling only 75% screen width, 50% screen height
            int textWidthLimit = (int) (renderDimensions.getWidth() * 0.75), // Only use 75% of the height
                    textHeightLimit = (int) (renderDimensions.getHeight() * 0.50); // Only use 50% of height

            Font boardFont = getScaledFontForArea(textWidthLimit, textHeightLimit, board.getInformation(), true);

            informationContainer.setFont(boardFont);
            informationContainer.setMaximumSize(new Dimension(textWidthLimit, textHeightLimit));


            addComponent(Box.createGlue());
            addComponent(informationContainer);
            addComponent(Box.createGlue());

        }

        // Message AND picture present
        if (hasImage == true && hasInformation == false && hasMessage == true) {

            // Message on top 1/3 of screen, filling width
            int messageWidthLimit = (int) (renderDimensions.getWidth()),
                    messageHeightLimit = (int) (renderDimensions.getHeight() / 3.0);

            //FIXME - displays fine, but still manages to wrap on occasion
            messageContainer.setFont(getScaledFontForArea(messageWidthLimit, messageHeightLimit, board.getMessage(), false));
            messageContainer.setMaximumSize(new Dimension(messageWidthLimit, messageHeightLimit));

            // Picture filling bottom 2/3
            imageContainer.setIcon(getScaledImage((ImageIcon) imageContainer.getIcon(),
                    (int) (renderDimensions.getWidth()),
                    (int) (renderDimensions.getHeight() / 3.0)));

            addComponent(Box.createGlue());
            addComponent(messageContainer);
            addComponent(Box.createGlue());
            addComponent(imageContainer);
            addComponent(Box.createGlue());

        }

        // Message AND information present
        if (hasImage == false && hasInformation == true && hasMessage == true) {

            // 50% of height for each of the message and information - use the same values
            int widthLimit = (int) (renderDimensions.getWidth()),
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
        if (hasImage == true && hasInformation == true && hasMessage == false) {

            // Picture in top 2/3 of screen, centered
            int imageWidthLimit = (int) (renderDimensions.getWidth()),
                    imageHeightLimit = (int) (renderDimensions.getHeight() / 3.0);

            imageContainer.setIcon(getScaledImage((ImageIcon) imageContainer.getIcon(), imageWidthLimit, imageHeightLimit));

            imageContainer.setMaximumSize(new Dimension(imageWidthLimit, imageHeightLimit));

            // Information in bottom 1/3 of screen, centered, no more than 75% of screen width

            int informationWidthLimit = (int) (renderDimensions.getWidth() * 0.75),
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
        if (hasImage == true && hasInformation == true && hasMessage == true) {

            // Both message and information must be in 1/3 of screen, centered, no more than 75% of screen width
            // So use the same values
            int textWidthLimit = (int) (renderDimensions.getWidth() * 0.75),
                    textHeightLimit = (int) (renderDimensions.getHeight() / 3.0);

            messageContainer.setFont(getScaledFontForArea(textWidthLimit, textHeightLimit, board.getMessage(), false));
            messageContainer.setMaximumSize(new Dimension(textWidthLimit, textHeightLimit));

            // Picture in middle 1/3 of screen, centered
            int imageWidthLimit = (int) (renderDimensions.getWidth()),
                    imageHeightLimit = (int) (renderDimensions.getHeight() / 3.0);

            imageContainer.setIcon(getScaledImage((ImageIcon) imageContainer.getIcon(), imageWidthLimit, imageHeightLimit));
            imageContainer.setMaximumSize(new Dimension(imageWidthLimit, imageHeightLimit));
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

    }

    /**
     * A helper method to retrieve an ImageIcon object from the internet, given a URL
     * @param url The URL of the image resource
     * @return An ImageIcon if the retrieval was successful, null in every other case.
     */
    public static ImageIcon getImageIconFromURL(URL url) {

        if (url == null) return null;

        BufferedImage img = null;
        try {
            img = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (img != null) return new ImageIcon(img);
        else return null;
    }

    /**
     * A helper method which retrieves an ImageIcon from a Base64 encoded string
     * @param imageString The string containing the Base64 encoded image data
     * @return An ImageIcon if decoding was successful, null in all other cases
     */
    public static ImageIcon getImageIconFromBase64(String imageString) {

        if (imageString == null) return null;

        byte[] decodedImage = Base64.getDecoder().decode(imageString.getBytes());

        return new ImageIcon(decodedImage);
    }

    /**
     * A helper method to scale an ImageIcon proportionally to a new size
     * @param src The image to be scaled
     * @param maxWidth The maximum width of the image after scaling
     * @param maxHeight The maximum height of the image after scaling
     * @return A scaled ImageIcon object
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
     * A helper method which is used to get a font which is an appropriate size to render within a given area
     * @param widthLimit The horizontal size of the rectangle in which rendering must occur
     * @param heightLimit The vertical size of the rectangle in which rendering must occur
     * @param stringToRender The string of text to render
     * @param allowWrap Whether the text is allowed to wrap within the rectangle
     * @return A font object representing a font of default size
     */
    Font getScaledFontForArea(int widthLimit, int heightLimit, String stringToRender, boolean allowWrap) {
        Font boardFont = new Font(DEFAULT_BILLBOARD_FONT, Font.BOLD, 10);

        int renderedStringWidth, renderedStringHeight;

        if(stringToRender.length() == 0) return boardFont;

        while (true) {
            boardFont = boardFont.deriveFont((float) (boardFont.getSize() + FONT_SIZE_INCREASE_RATE));

            FontMetrics fontMetrics = headlessCanvas.getFontMetrics(boardFont);

            // Calculate the number of rows required to render the text in a given width (text width / width of screen)
            int requiredRowsToRender = (int) Math.ceil((float) fontMetrics.stringWidth(stringToRender) / (float) widthLimit);

            //Determine width and height of the string
            renderedStringHeight = (fontMetrics.getHeight() + fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent()) * requiredRowsToRender;
            renderedStringWidth = fontMetrics.stringWidth(stringToRender);

            //Wrapped scaling
            if (allowWrap) {
                // If the information, wrapped, is the right height for the screen, we should be done
                if (renderedStringHeight >= heightLimit * PADDING_PERCENTAGE) break;

                // If we've gone too wide for the screen...
                if (renderedStringWidth / requiredRowsToRender >= widthLimit) {
                    // Check whether another line would cause us to blow out of our boundaries
                    if ((requiredRowsToRender + 1) * fontMetrics.getHeight() <= heightLimit) {
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
                if (renderedStringWidth >= widthLimit * PADDING_PERCENTAGE || renderedStringHeight >= heightLimit * PADDING_PERCENTAGE)
                    break;
            }
        }

        return boardFont;
    }

    /**
     * A helper method which adds a component to the Rendered Billboard
     * @param c The component to be added to the Rendered Billboard
     */
    void addComponent(Component c) {
        this.add(c);
    }


}

