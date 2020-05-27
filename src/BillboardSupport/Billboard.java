package BillboardSupport;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Base64;

//TODO - Implement serializable
public class Billboard implements Serializable {

    public Color getBackgroundColour() {
        return backgroundColour;
    }

    public void setBackgroundColour(Color backgroundColour) {
        this.backgroundColour = backgroundColour;
    }

    public Color getMessageColour() {
        return messageColour;
    }

    public void setMessageColour(Color messageColour) {
        this.messageColour = messageColour;
    }

    public Color getInformationColour() {
        return informationColour;
    }

    public void setInformationColour(Color informationColour) {
        this.informationColour = informationColour;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public ImageIcon getBillboardImage() {
        return billboardImage;
    }

    public void setBillboardImage(ImageIcon billboardImage) {
        this.billboardImage = billboardImage;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public int getBillboardDatabaseKey() {
        return billboardDatabaseKey;
    }

    public void setBillboardDatabaseKey(int billboardDatabaseKey) {
        this.billboardDatabaseKey = billboardDatabaseKey;
    }

    private Color backgroundColour, messageColour, informationColour;

    private static Color DEFAULT_COLOUR = Color.WHITE;

    private String message, information;

    private ImageIcon billboardImage;

    // ADD variable to track who owns the Billboard

    private LocalDateTime scheduledTime;
    private int duration, repeatInterval; // in seconds - controlled at user level

    private int billboardDatabaseKey;



    /** New Billboard Object from scratch
     * @param backgroundColour The colour of the Billboard background
     * @param messageColour The colour of the text which displays the 'message' string.
     * @param informationColour The colour of the text which displays the 'information' string. Supplied by the 'colour' node int he XML Schema
     * @param message The primary text of the billboard. Should be displayed in a clearly visible, large font size which displays on one line with no breaks.
     * @param information Used tos how larger amounts of text information which can be broken across multiple lines for display purposes.
     * @param image The image to be displayed by the Billboard
     * @param scheduledTime The time the billboard is first scheduled to display
     * @param duration The amount of time in seconds to display the Billboard
     * @param repeatInterval The frequency, expressed in seconds, that the billboard repeats
     */

    public Billboard(Color backgroundColour, Color messageColour, Color informationColour, String message, String information, ImageIcon image, LocalDateTime scheduledTime, int duration, int repeatInterval) {
        this.backgroundColour = backgroundColour;
        this.messageColour = messageColour;
        this.informationColour = informationColour;
        this.message = message;
        this.information = information;
        this.billboardImage = image;
        this.scheduledTime = scheduledTime;
        this.duration = duration;
        this.repeatInterval = repeatInterval;
    }

    /** Create Billboard Object from Database field
     * @param backgroundColour The colour of the Billboard background
     * @param messageColour The colour of the text which displays the 'message' string.
     * @param informationColour The colour of the text which displays the 'information' string. Supplied by the 'colour' node int he XML Schema
     * @param message The primary text of the billboard. Should be displayed in a clearly visible, large font size which displays on one line with no breaks.
     * @param information Used tos how larger amounts of text information which can be broken across multiple lines for display purposes.
     * @param image The image to be displayed by the Billboard
     * @param scheduledTime The time the billboard is first scheduled to display
     * @param duration The amount of time in seconds to display the Billboard
     * @param repeatInterval The frequency, expressed in seconds, that the billboard repeats
     * @param billboardDatabaseKey The key which corresponds to this Billboard in the database
     */
    public Billboard(Color backgroundColour, Color messageColour, Color informationColour, String message, String information, ImageIcon image, LocalDateTime scheduledTime, int duration, int repeatInterval, int billboardDatabaseKey) {
        this.backgroundColour = backgroundColour;
        this.messageColour = messageColour;
        this.informationColour = informationColour;
        this.message = message;
        this.information = information;
        this.billboardImage = image;
        this.scheduledTime = scheduledTime;
        this.duration = duration;
        this.repeatInterval = repeatInterval;
        this.billboardDatabaseKey = billboardDatabaseKey;
    }

    /** Create Billboard from Billboard XML File
     *
     * @param billboardXML An XML String which conforms to the Billboard XML Spec.
     */

    public Billboard(String billboardXML) {
        //TODO - read in Billboard XML
    }

    public Billboard(){

    }

    public static Billboard errorBillboard(){
        Billboard errorBillboard = new Billboard();
        errorBillboard.setMessage("Error: Could not connect to server");

        return errorBillboard;
    }


    public ImageIcon getImageIconFromURL(String url) throws Exception {
        BufferedImage img = ImageIO.read(new URL(url));
        return new ImageIcon(img);
    }

    public ImageIcon getImageIconFromBase64(String imageString) {

        byte[] decodedImage = Base64.getDecoder().decode(imageString.getBytes());

        return new ImageIcon(decodedImage);
    }


}
