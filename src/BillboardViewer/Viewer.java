package BillboardViewer;

import BillboardSupport.Billboard;
import BillboardSupport.RenderedBillboard;
import BillboardSupport.DummyBillboards;

import java.awt.*;
import java.awt.event.*;

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

public class Viewer extends JFrame implements ActionListener   {

    private static Dimension screenSize;
    private RenderedBillboard displayedBillboard;
    private Billboard currentBillboard;

    public static javax.swing.Timer refreshTimer;

    Viewer (String arg0){
        super(arg0);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.setUndecorated(true);

        Dimension screenSize = new Dimension(   (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                                                (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());

        this.setSize(screenSize);
        this.setVisible(true);

    }

    public static void main(String[] args) {

        screenSize = new Dimension(   (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Viewer viewer = new Viewer("Test");

                //Register the glass pane for key and mouse events
                Component component = viewer.getGlassPane();
                component.addKeyListener(new MyKeyboardHandler());
                component.addMouseListener(new MyMouseHandler());


                // Sort out the refresh timer
                refreshTimer = new Timer(15*1000, viewer); // 15 * 1000ms = 15 seconds
                refreshTimer.setInitialDelay(0); // Start as quick as possible
                refreshTimer.start(); //Start rendering ASAP
            }
        });
    }

    static int counter;
    @Override
    public void actionPerformed(ActionEvent e) {
        // Timer handling
        if(e.getSource() == refreshTimer){

            // TODO - implement network retrieval of billboard
            currentBillboard = DummyBillboards.messagePictureAndInformationBillboard();
            currentBillboard.setMessage("Have ticked over " + counter + " times");
            counter++;

            // Clear the deck to avoid memory blowout over time
            if(displayedBillboard != null) this.getContentPane().remove(displayedBillboard);

            // Insert the new Billboard
            displayedBillboard = new RenderedBillboard(currentBillboard, screenSize);
            this.add(displayedBillboard);

            // Return focus to the glasspane for event handling
            this.getGlassPane().setFocusable(true);
            this.getGlassPane().requestFocus();
            this.getGlassPane().setVisible(true);

        }
    }
}
