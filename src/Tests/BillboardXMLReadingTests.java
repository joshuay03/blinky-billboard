package Tests;

import BillboardSupport.Billboard;
import BillboardSupport.DummyBillboards;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions.*;

import java.io.File;

public class BillboardXMLReadingTests {

    Billboard getBillboardFromPath(String path){
        File billboard = new File(path);

        System.out.println(billboard.toString());

        return null; // TODO - adapt to use the Billboard.getBillboardFromXML function
    }

    @Test
    void readInMessageOnlyBillboard(){

        Billboard parsedBillboard = getBillboardFromPath("Billboard_Examples/1.xml");

        Assertions.assertEquals(parsedBillboard, DummyBillboards.messageOnlyBillboard());
    }

    @Test
    void readInpPictureOnlyBillboard(){

    }

    @Test
    void readInInformationOnlyBillboard(){
        Billboard parsedBillboard = getBillboardFromPath("Billboard_Examples/2.xml");

        Assertions.assertEquals(parsedBillboard, DummyBillboards.messageOnlyBillboard());
    }

    @Test
    void readInMessageAndPictureBillboard(){

    }

    @Test
    void readInMessageAndInformationBillboard(){

    }

    @Test
    void readInPictureAndInformationBillboard(){

    }

}
