package BillboardViewer;

import BillboardSupport.Billboard;
import BillboardSupport.RenderedBillboard;
import BillboardSupport.DummyBillboards;
import Client.ClientConnector;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

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

public class Viewer extends JFrame {

    private RenderedBillboard displayedBillboard;
    private Billboard currentBillboard;
    public ClientConnector connector;

    Viewer (String arg0){
        super(arg0);


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.setUndecorated(true);

        this.setBackground(Color.BLACK);

        this.addKeyListener(new MyKeyboardHandler());
        this.addMouseListener(new MyMouseHandler());

        Dimension halfSize = new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2, (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2);

        this.setSize(halfSize);
        this.setVisible(true);


        displayedBillboard = new RenderedBillboard(DummyBillboards.informationOnlyBillboard(), halfSize);
        this.add(displayedBillboard);

        System.out.println();

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
