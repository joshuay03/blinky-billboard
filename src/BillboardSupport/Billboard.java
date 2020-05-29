package BillboardSupport;

import org.w3c.dom.*;
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

public class Billboard implements Serializable {

    // Creates an empty billboard
    public Billboard() {
    }

    //<editor-fold desc="GETTERS, SETTERS & MEMBERS">


    public int getBillboardDatabaseKey() {
        return billboardDatabaseKey;
    }

    public void setBillboardDatabaseKey(int billboardDatabaseKey) {
        this.billboardDatabaseKey = billboardDatabaseKey;
    }

    public URL getImageURL() {
        return imageURL;
    }

    public void setImageURL(URL imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

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
    //</editor-fold>

    private Color backgroundColour, messageColour, informationColour;

    private static Color DEFAULT_COLOUR = Color.WHITE;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreator(String username){
        this.creator = username;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    private String message, information;

    private URL imageURL;

    private String imageData;

    private String creator;

    private int billboardDatabaseKey;

    private Schedule schedule;



    /** New Billboard Object from scratch
     * @param backgroundColour The colour of the Billboard background
     * @param messageColour The colour of the text which displays the 'message' string.
     * @param informationColour The colour of the text which displays the 'information' string. Supplied by the 'colour' node int he XML Schema
     * @param message The primary text of the billboard. Should be displayed in a clearly visible, large font size which displays on one line with no breaks.
     * @param information Used tos how larger amounts of text information which can be broken across multiple lines for display purposes.
     * @param imageURL The URL of the image to be displayed by the Billboard
     */

    public Billboard(Color backgroundColour, Color messageColour, Color informationColour, String message, String information, URL imageURL) {
        this.backgroundColour = backgroundColour;
        this.messageColour = messageColour;
        this.informationColour = informationColour;
        this.message = message;
        this.information = information;
        this.imageURL = imageURL;
    }

    /** New Billboard Object from scratch
     * @param backgroundColour The colour of the Billboard background
     * @param messageColour The colour of the text which displays the 'message' string.
     * @param informationColour The colour of the text which displays the 'information' string. Supplied by the 'colour' node int he XML Schema
     * @param message The primary text of the billboard. Should be displayed in a clearly visible, large font size which displays on one line with no breaks.
     * @param information Used tos how larger amounts of text information which can be broken across multiple lines for display purposes.
     * @param imageData The Base64 byte string of the image to be displayed by the Billboard
     */

    public Billboard(Color backgroundColour, Color messageColour, Color informationColour, String message, String information, String imageData) {
        this.backgroundColour = backgroundColour;
        this.messageColour = messageColour;
        this.informationColour = informationColour;
        this.message = message;
        this.information = information;
        this.imageData = imageData;
    }

    /** Create Billboard from Billboard XML File
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

            NodeList messageNodes = doc.getElementsByTagName("message");
            if (messageNodes.getLength() > 0) {
                billboard.message = messageNodes.item(0).getTextContent();

                if (messageNodes.item(0).getAttributes().getLength() > 0) {
                    billboard.messageColour = Color.decode(messageNodes.item(0).getAttributes().item(0).getTextContent());
                }
            }

            NodeList pictureNodes = doc.getElementsByTagName("picture");
            if (pictureNodes.getLength() > 0) {
                if (pictureNodes.item(0).hasAttributes()) {
                    if (pictureNodes.item(0).getAttributes().getNamedItem("url") != null) {
                        billboard.imageURL = new URL(pictureNodes.item(0).getAttributes().item(0).getTextContent());
                    }
                    else if (pictureNodes.item(0).getAttributes().getNamedItem("data") != null) {
                        billboard.imageData = pictureNodes.item(0).getAttributes().item(0).getTextContent();
                    }
                }
            }

            NodeList informationNodes = doc.getElementsByTagName("information");
            if (informationNodes.getLength() > 0) {
                billboard.information = informationNodes.item(0).getTextContent();

                if (informationNodes.item(0).getAttributes().getLength() > 0) {
                    billboard.informationColour = Color.decode(informationNodes.item(0).getAttributes().item(0).getTextContent());
                }
            }

        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("That does not appear to be valid billboard data!");
        }
        return billboard;
    }

    private static int ALPHA_MASK = 0x00FFFFFF;
    public Document getXMLRepresentation(){

        Document billboardXMLRep = null;
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            billboardXMLRep = documentBuilder.newDocument();

            //Billboard element
            Element root = billboardXMLRep.createElement("billboard");
            billboardXMLRep.appendChild(root);

            // Background colour, if any
            if(this.backgroundColour != null) {
                Attr backgroundHexRep = billboardXMLRep.createAttribute("background");
                backgroundHexRep.setValue("#" + Integer.toHexString(this.backgroundColour.getRGB() & ALPHA_MASK)); // Mask out the alpha channel
                root.setAttributeNode(backgroundHexRep);
            }

            //Message ------------------------------------------------
            Element msg = null;
            if(this.message != null) {
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
            if(this.information != null) {
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
            //  N.B. Because we only ever store the ImageIcon data (and we don't retain the URL) when we read a Billboard in, we only have to deal with writing out
            // Base64 encoded image data
            Element image = null;
            if (this.imageData != null ^ this.imageURL != null) {
                image = billboardXMLRep.createElement("picture");
                Attr imageNode = null;

                if(this.imageData != null) {
                    // Sort out the data for writing
                    imageNode = billboardXMLRep.createAttribute("data");
                    imageNode.setValue(imageData);
                } else if(this.imageURL != null){
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

    public String getBillboardImage(){
        if(imageURL != null){
            return imageURL.toString();
        } else if (imageData != null){
            return imageData;
        }
        return null;
    }

    public static Billboard errorBillboard(){
        Billboard errorBillboard = new Billboard();
        errorBillboard.message = "Error: Could not connect to server";

        return errorBillboard;
    }

    @Override
    public boolean equals(Object object){

        Billboard comparator = (Billboard) object;

        Collator collator = Collator.getInstance(Locale.ENGLISH);

        //Check whether all the constituent parts of the billboard match
        return this.backgroundColour.equals(comparator.backgroundColour) &&
                this.messageColour.equals(comparator.messageColour) &&
                this.informationColour.equals(comparator.informationColour) &&
                collator.compare(this.message, comparator.message) == 0 &&
                collator.compare(this.information, comparator.information) == 0 &&
                imageData != null ? collator.compare(this.imageData, comparator.imageData) == 0 : false ||
                imageURL == null || collator.compare(this.imageURL.toString(), comparator.imageURL.toString()) == 0;
    }

    public String getCreator() { return creator; }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
