package BillboardViewer;

import BillboardSupport.Billboard;
import BillboardSupport.RenderedBillboard;
import BillboardSupport.DummyBillboards;

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

public class Viewer extends JFrame {

    private RenderedBillboard displayedBillboard;
    private Billboard currentBillboard;

    Viewer (String arg0){
        super(arg0);

        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.setUndecorated(true);

        this.setBackground(Color.BLACK);

        this.addKeyListener(new MyKeyboardHandler());
        this.addMouseListener(new MyMouseHandler());

        displayedBillboard = new RenderedBillboard(DummyBillboards.messagePictureAndInformationBillboard(), Toolkit.getDefaultToolkit().getScreenSize());
        this.getContentPane().add(displayedBillboard);

        repaint();
        this.setVisible(true);

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
        JFrame.setDefaultLookAndFeelDecorated(false);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Viewer("Test");

            }
        });
    }

}
