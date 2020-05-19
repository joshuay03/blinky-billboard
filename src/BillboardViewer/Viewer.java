package BillboardViewer;

import BillboardSupport.Billboard;
import BillboardSupport.RenderedBillboard;
import BillboardSupport.DummyBillboards;

import java.awt.*;
import java.util.logging.Handler;

import javax.swing.*;

import BillboardViewer.*;

public class Viewer extends JFrame {

    private RenderedBillboard displayedBillboard;
    private Billboard currentBillboard;

    Viewer (String arg0){
        super(arg0);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.setUndecorated(true);

        Dimension screenSize = new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());

        this.setSize(screenSize);
        this.setVisible(true);

        displayedBillboard = new RenderedBillboard(DummyBillboards.messagePictureAndInformationBillboard(), screenSize);
        this.add(displayedBillboard);

    }

    private void refreshHandler(){
        //TODO - Make billboard refresh every 15 sec
        while(true){

            try{
                Thread.sleep(15 * 100);
            } catch (Exception e){
                System.out.println("Something went wrong: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Viewer("Test");

            }
        });
    }

}
