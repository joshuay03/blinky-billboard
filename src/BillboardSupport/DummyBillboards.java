package BillboardSupport;

import java.awt.*;
import java.net.URL;

/**
 * A class designed to provide billboards which reproduce different combinations of data
 */
public class DummyBillboards {

    static String dummyImage = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAQCAIAAAD4YuoOAAAAKXRFWHRDcmVhdGlvbiBUaW1lAJCFIDI1IDMgMjAyMCAwOTowMjoxNyArMDkwMHlQ1XMAAAAHdElNRQfkAxkAAyQ8nibjAAAACXBIWXMAAAsSAAALEgHS3X78AAAABGdBTUEAALGPC/xhBQAAAS5JREFUeNq1kb9KxEAQxmcgcGhhJ4cnFwP6CIIiPoZwD+ALXGFxj6BgYeU7BO4tToSDFHYWZxFipeksbMf5s26WnAkJki2+/c03OzPZDRJNYcgVwfsU42cmKi5YjS1s4p4DCrkBPc0wTlkdX6bsG4hZQOj3HRDLHqh08U4Adb/zgEMtq5RuH3Axd45PbftdB2wO5OsWc7pOYaOeOk63wYfdFtL5qldB34W094ZfJ+4RlFldTrmW/ZNbn2g0of1vLHdZq77qSDCaSAsLf9kXh9w44PNoR/YSPHycEmbIOs5QzBJsmDHrWLPeF24ZkCe6ZxDCOqHcmxmsr+hsicahss+n8vYb8NHZPTJxi/RGC5IqbRwqH6uxVTX+5LvHtvT/V/R6PGh/iF4GHoBAwz7RD26spwq6Amh/AAAAAElFTkSuQmCC";

    /**
     * Method which instantiates a default billboard
     * @return a default billboard
     */
    public static Billboard defaultBillboard() {
        Billboard b = new Billboard();
        b.setBillboardName("Default");
        b.setBackgroundColour(Color.WHITE);
        b.setMessageColour(Color.BLACK);
        b.setInformationColour(Color.DARK_GRAY);
        b.setMessage("");
        b.setInformation("");
        b.setImageData(dummyImage);
        return b;
    }

    /**
     * Method which instantiates a billboard with only a message
     * @return a default billboard
     */
    public static Billboard messageOnlyBillboard() {
        Billboard b = new Billboard();
        b.setBillboardName("MessageOnly");
        b.setMessage("Basic message-only billboard");

        return b;
    }

    /**
     * Method which instantiates a billboard with only a picture
     * @return a default billboard
     */
    public static Billboard pictureOnlyBillboard() {
        Billboard b = new Billboard();
        b.setBillboardName("PictureOnly");
        b.setImageData(dummyImage);

        return b;
    }

    /**
     * Method which instantiates a billboard with only information data
     * @return a default billboard
     */
    public static Billboard informationOnlyBillboard() {
        Billboard b = new Billboard();
        b.setBillboardName("InformationOnly");
        b.setInformation("Billboard with an information tag and nothing else. Note that the text is word-wrapped. The quick brown fox jumped over the lazy dogs.");

        return b;
    }

    /**
     * Method which instantiates a billboard with a message and a picture
     * @return a default billboard
     */
    public static Billboard messageAndPictureBillboard() {
        Billboard b = new Billboard();
        b.setBillboardName("MessageAndPic");
        b.setMessage("Billboard with message and picture with data attribute");
        b.setImageData(dummyImage);

        return b;
    }

    /**
     * Method which instantiates a billboard with a message and information
     * @return a default billboard
     */
    public static Billboard messageAndInformationBillboard() {
        Billboard b = new Billboard();
        b.setBillboardName("MessageAndInfo");
        b.setMessage("Billboard with message and info");
        b.setInformation("Billboard with a message tag, an information tag, but no picture tag. The message is centred within the top half of the screen while the information is centred within the bottom half.");

        return b;
    }

    /**
     * Method which instantiates a billboard with a picture and information data
     * @return a default billboard
     */
    public static Billboard pictureAndInformationBillboard() {
        Billboard b = new Billboard();
        b.setBillboardName("PicAndInfo");
        b.setInformation("Billboard with message and picture with data attribute");
        b.setImageData(dummyImage);

        return b;
    }

    /**
     * Method which instantiates a billboard with a message, picture, and information data
     * @return a default billboard
     */
    public static Billboard messagePictureAndInformationBillboard() {
        Billboard b = new Billboard();
        b.setBillboardName("MessagePicAndInfo");
        b.setInformation("This billboard has a message tag, a picture tag (linking to a URL with a GIF image) and an information tag. The picture is drawn in the centre and the message and information text are centred in the space between the top of the image and the top of the page, and the space between the bottom of the image and the bottom of the page, respectively.");
        b.setMessage("Billboard with message, GIF and information");
        try {
            b.setImageURL(new URL("https://cloudstor.aarnet.edu.au/plus/s/A26R8MYAplgjUhL/download"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

}
