package BillboardSupport;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.Collator;
import java.util.Locale;

/**
 * An object which contains a representation of a billboard, and all of its properties.
 */
public class Billboard implements Serializable {

    private static Color DEFAULT_COLOUR = Color.WHITE;

    //<editor-fold desc="GETTERS, SETTERS & MEMBERS">
    private static int ALPHA_MASK = 0x00FFFFFF;
    private Color backgroundColour, messageColour, informationColour;
    private String message, information;
    private URL imageURL;
    private String imageData;
    private String creator;
    private String billboard_name;
    private Schedule schedule;

    /**
     * Creates an empty billboard with none of the fields initialised.
     */
    public Billboard() {
        this.backgroundColour = null;
        this.messageColour = null;
        this.information = null;
        this.message = null;
        this.information = null;
        this.imageURL = null;
        this.imageData = null;
        this.creator = null;
        this.schedule = null;
    }

    /**
     * New Billboard Object from scratch, given an Image URL
     *
     * @param name              The name of the billboard
     * @param backgroundColour  The colour of the Billboard background
     * @param messageColour     The colour of the text which displays the 'message' string.
     * @param informationColour The colour of the text which displays the 'information' string. Supplied by the 'colour' node int he XML Schema
     * @param message           The primary text of the billboard. Should be displayed in a clearly visible, large font size which displays on one line with no breaks.
     * @param information       Used tos how larger amounts of text information which can be broken across multiple lines for display purposes.
     * @param imageURL          The URL of the image to be displayed by the Billboard
     */

    public Billboard(String name, Color backgroundColour, Color messageColour, Color informationColour, String message, String information, URL imageURL) {
        this.billboard_name = name;
        this.backgroundColour = backgroundColour;
        this.messageColour = messageColour;
        this.informationColour = informationColour;
        this.message = message;
        this.information = information;
        this.imageURL = imageURL;
    }

    /**
     * New Billboard Object from scratch, given Base64 Image Data
     *
     * @param name              The name of the billboard
     * @param backgroundColour  The colour of the Billboard background
     * @param messageColour     The colour of the text which displays the 'message' string.
     * @param informationColour The colour of the text which displays the 'information' string. Supplied by the 'colour' node int he XML Schema
     * @param message           The primary text of the billboard. Should be displayed in a clearly visible, large font size which displays on one line with no breaks.
     * @param information       Used tos how larger amounts of text information which can be broken across multiple lines for display purposes.
     * @param imageData         The Base64 byte string of the image to be displayed by the Billboard
     */

    public Billboard(String name, Color backgroundColour, Color messageColour, Color informationColour, String message, String information, String imageData) {
        this.billboard_name = name;
        this.backgroundColour = backgroundColour;
        this.messageColour = messageColour;
        this.informationColour = informationColour;
        this.message = message;
        this.information = information;
        this.imageData = imageData;
    }

    /**
     * Create Billboard from Billboard XML File
     *
     * @param billboardXMLFile An XML File which conforms to the Billboard XML Spec.
     */
    public static Billboard getBillboardFromXML(File billboardXMLFile) {
        Billboard billboard = new Billboard();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(billboardXMLFile);
            doc.getDocumentElement().normalize();

            // Find the billboard element
            NodeList billboardNodes = doc.getElementsByTagName("billboard");
            if (billboardNodes.getLength() > 0) {

                // While we're here, get the colour
                if (billboardNodes.item(0).getAttributes().getLength() > 0) {
                    billboard.backgroundColour = Color.decode(billboardNodes.item(0).getAttributes().item(0).getTextContent());
                }
            }
            //If there are no elements with "billboard" in the tag name, it's probably a dud file
            else {
                System.out.println("Not a valid billboard XML File");
                return null;
            }

            // Handle the message, if any
            NodeList messageNodes = doc.getElementsByTagName("message");
            if (messageNodes.getLength() > 0) {
                billboard.message = messageNodes.item(0).getTextContent();

                if (messageNodes.item(0).getAttributes().getLength() > 0) {
                    billboard.messageColour = Color.decode(messageNodes.item(0).getAttributes().item(0).getTextContent());
                }
            }

            //Handle the picture, if any
            NodeList pictureNodes = doc.getElementsByTagName("picture");
            if (pictureNodes.getLength() > 0) {
                if (pictureNodes.item(0).hasAttributes()) {
                    if (pictureNodes.item(0).getAttributes().getNamedItem("url") != null) {
                        billboard.imageURL = new URL(pictureNodes.item(0).getAttributes().item(0).getTextContent());
                    } else if (pictureNodes.item(0).getAttributes().getNamedItem("data") != null) {
                        billboard.imageData = pictureNodes.item(0).getAttributes().item(0).getTextContent();
                    }
                }
            }

            //Handle information, if any
            NodeList informationNodes = doc.getElementsByTagName("information");
            if (informationNodes.getLength() > 0) {
                billboard.information = informationNodes.item(0).getTextContent();

                if (informationNodes.item(0).getAttributes().getLength() > 0) {
                    billboard.informationColour = Color.decode(informationNodes.item(0).getAttributes().item(0).getTextContent());
                }
            }

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
        // TODO - consider letting the SAXException go out to the GUI so that the user can be informed the file was no good, otherwise they will just get an empty billboard
        catch (SAXException e) {
            System.out.println("That does not appear to be valid billboard data!");
        }
        return billboard;
    }
    //</editor-fold>

    /**
     * A class to get a Billboard configured for error cases (primarily used by Viewer)
     * @return Billboard object
     */
    public static Billboard errorBillboard() {
        Billboard errorBillboard = new Billboard();
        errorBillboard.message = "Error: Could not connect to server";

        return errorBillboard;
    }

    /**
     * @return
     */
    public boolean isScheduled() {
        return schedule != null;
    }
    /**
     * @return
     */
    public String getBillboardName() {
        return billboard_name;
    }
    /**
     * @return
     */
    public void setBillboardName(String billboard_name) {
        this.billboard_name = billboard_name;
    }
    /**
     * @return
     */
    public URL getImageURL() {
        return imageURL;
    }
    /**
     * @return
     */
    public void setImageURL(URL imageURL) {
        this.imageURL = imageURL;
    }
    /**
     * @return
     */
    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
    /**
     * @return The background color of the billboard
     */
    public Color getBackgroundColour() {
        return backgroundColour;
    }
    /**
     * Sets the background color of the billboard
     */
    public void setBackgroundColour(Color backgroundColour) {
        this.backgroundColour = backgroundColour;
    }
    /**
     * @return the color of the messages
     */
    public Color getMessageColour() {
        return messageColour;
    }
    /**
     * Set the color of the messages
     */
    public void setMessageColour(Color messageColour) {
        this.messageColour = messageColour;
    }
    /**
     * @return the color of the information
     */
    public Color getInformationColour() {
        return informationColour;
    }
    /**
     * Set the color of the information
     */
    public void setInformationColour(Color informationColour) {
        this.informationColour = informationColour;
    }
    /**
     * @return the message data
     */
    public String getMessage() {
        return message;
    }
    /**
     * Set the message data
     */
    public void setMessage(String message) {
        this.message = message;
    }
    /**
     * @return The information data
     */
    public String getInformation() {
        return information;
    }
    /**
     * set the information data
     */
    public void setInformation(String information) {
        this.information = information;
    }

    /**
     * A method to get an XML Representation of a Billboard which can be written to disk
     * @return Document object
     */
    public Document getXMLRepresentation() {

        Document billboardXMLRep = null;
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            billboardXMLRep = documentBuilder.newDocument();

            //Create the root Billboard element
            Element root = billboardXMLRep.createElement("billboard");
            billboardXMLRep.appendChild(root);

            // Background colour, if any
            if (this.backgroundColour != null) {
                Attr backgroundHexRep = billboardXMLRep.createAttribute("background");
                backgroundHexRep.setValue("#" + Integer.toHexString(this.backgroundColour.getRGB() & ALPHA_MASK)); // Mask out the alpha channel
                root.setAttributeNode(backgroundHexRep);
            }

            //Message ------------------------------------------------
            Element msg = null;
            if (this.message != null) {
                msg = billboardXMLRep.createElement("message");
                msg.appendChild(billboardXMLRep.createTextNode(this.message));


                // Message colour, if any
                if (this.messageColour != null) {
                    Attr msgColor = billboardXMLRep.createAttribute("colour");
                    msgColor.setValue("#" + Integer.toHexString(this.messageColour.getRGB() & ALPHA_MASK)); // Mask out the alpha channel
                    msg.setAttributeNode(msgColor);
                }
                root.appendChild(msg);
            }

            // Information ------------------------------------------------
            Element info = null;
            if (this.information != null) {
                info = billboardXMLRep.createElement("information");
                info.appendChild(billboardXMLRep.createTextNode(this.information));


                //Info colour, if any
                if (this.informationColour != null) {
                    Attr infoColour = billboardXMLRep.createAttribute("colour");
                    infoColour.setValue("#" + Integer.toHexString(this.informationColour.getRGB() & ALPHA_MASK)); // Mask out the alpha channel
                    info.setAttributeNode(infoColour);
                }
                root.appendChild(info);
            }

            // Image ------------------------------------------------
            Element image = null;
            if (this.imageData != null ^ this.imageURL != null) {
                image = billboardXMLRep.createElement("picture");
                Attr imageNode = null;

                if (this.imageData != null) {
                    // Sort out the data for writing
                    imageNode = billboardXMLRep.createAttribute("data");
                    imageNode.setValue(imageData);
                } else if (this.imageURL != null) {
                    imageNode = billboardXMLRep.createAttribute("url");
                    imageNode.setValue(imageURL.toString());
                }
                image.setAttributeNode(imageNode);
                root.appendChild(image);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return billboardXMLRep;
    }

    /**
     * A special getter to handle the URL/Base64 image storage dichotomy
     * @return Either a string representation of the URL, or a Base64 encoded string, or null if neither of the above
     * are present
     */
    public String getBillboardImage() {
        if (imageURL != null) {
            return imageURL.toString();
        } else if (imageData != null) {
            return imageData;
        }
        return null;
    }

    /**
     * Compare two billboard objects
     * @param object
     * @return true/false
     */
    @Override
    public boolean equals(Object object) {

        Billboard comparator = (Billboard) object;

        Collator collator = Collator.getInstance(Locale.ENGLISH);

        //Check whether all the constituent parts of the billboard match
        return this.backgroundColour.equals(comparator.backgroundColour) &&
                this.messageColour.equals(comparator.messageColour) &&
                this.informationColour.equals(comparator.informationColour) &&
                collator.compare(this.message, comparator.message) == 0 &&
                collator.compare(this.information, comparator.information) == 0 &&
                imageData != null ? collator.compare(this.imageData, comparator.imageData) == 0 : imageURL == null || collator.compare(this.imageURL.toString(), comparator.imageURL.toString()) == 0;
    }

    /**
     * @return the creato of the billboard
     */
    public String getCreator() {
        return creator;
    }
    /**
     * Set the creator of the billboard
     */
    public void setCreator(String username) {
        this.creator = username;
    }
    /**
     * @return the billboard's schedule
     */
    public Schedule getSchedule() {
        return schedule;
    }
    /**
     * Set the billboard's schedule
     */
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
