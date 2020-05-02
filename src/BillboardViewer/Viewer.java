package BillboardViewer;

import BillboardSupport.Billboard;
import BillboardSupport.DummyBillboards;
import BillboardSupport.RenderedBillboard;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.tools.Tool;

class MyMouseHandler extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1){
            System.exit(1);
        }
    }

}

class MyKeyboardHandler extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            System.exit(1);
        }
    }
}

public class Viewer {

    private JPanel billboardDisplay;

    private JFrame frame;

    public static void main(String[] args) {

        Viewer viewer =  new Viewer();
        viewer.initialSetup();

        viewer.refreshHandler();
    }

    private void initialSetup() {
        frame = new JFrame("Viewer");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.pack();

        // Set to full-screen - N.B. this is done in a very lazy way. There are more advanced ways to do it which respect multi-screen setups: https://docs.oracle.com/javase/6/docs/api/java/awt/Toolkit.html#getScreenSize()
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        // Register window, keyboard and mouse handlers
        frame.addMouseListener(new MyMouseHandler());
        frame.addKeyListener(new MyKeyboardHandler());

        frame.setVisible(true);

    }

    private void refreshHandler(){

        while (true) {
            try {
                // Contact the server

                // If there is no error with contacting the server, proceed to populate view with Billboard
                // Dummy object
                DummyBillboards dummyBillboards = new DummyBillboards();
                Billboard bb = dummyBillboards.informationOnlyBillboard();


                // Try to render
                frame.getContentPane().add(new RenderedBillboard(bb, Toolkit.getDefaultToolkit().getScreenSize()));


            } catch (Exception e) {

            }

            try {
                // Wait 15 seconds
                Thread.sleep(15 * 100);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


}
