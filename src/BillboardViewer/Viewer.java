package BillboardViewer;

import BillboardSupport.Billboard;
import BillboardSupport.RenderedBillboard;
import Client.ClientConnector;
import SocketCommunication.Request;
import SocketCommunication.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * A subclass of MouseAdapter to handle mouse input
 */
class MyMouseHandler extends MouseAdapter {
    /**
     * A method to handle mouse click events
     * @param e A MouseEvent notified by the runtime
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // Left click
        if (e.getButton() == MouseEvent.BUTTON1) {
            System.exit(1);
        }
    }
}

/**
 * A subclass of KeyAdapter to handle keyboard input
 */
class MyKeyboardHandler extends KeyAdapter {
    /**
     * A method to keyboard events
     * @param e A KeyEvent notified by the runtime
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(1);
        }
    }
}

/**
 * A program to retrieve and display Billboards from the Server
 */
public class Viewer extends JFrame implements ActionListener {

    public static javax.swing.Timer refreshTimer;
    private static Dimension screenSize;
    public ClientConnector connector;
    private RenderedBillboard displayedBillboard;
    private Billboard currentBillboard;

    Viewer(String arg0) {
        super(arg0);

        // The viewer must render full screen
        Dimension screenSize = new Dimension(   (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                                                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        this.setSize(screenSize);

        // Set up the viewer's JFrame to have minimal decoration, and to exit when a close action is received
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setUndecorated(true);

        this.setVisible(true);

        // Initialise the client connector which will be called to interact with the Server
        this.connector = new ClientConnector("properties.txt");
    }

    /**
     * Main loop of the Viewer Program which handles setup, and commences the refresh handling
     * @param args No arguments handled.
     */
    public static void main(String[] args) {

        screenSize = new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Viewer viewer = new Viewer("Test");

                //Register the glass pane for key and mouse events
                Component component = viewer.getGlassPane();
                component.addKeyListener(new MyKeyboardHandler());
                component.addMouseListener(new MyMouseHandler());

                // Sort out the refresh timer
                refreshTimer = new Timer(15 * 1000, viewer); // 15 * 1000ms = 15 seconds
                refreshTimer.setInitialDelay(0); // Start as quick as possible
                refreshTimer.start(); //Start rendering ASAP
            }
        });
    }

    /**
     * A method which principally handles timer tick events to refresh the Viewer
     * @param e An ActionEvent provided by the runtime
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Timer handling
        if (e.getSource() == refreshTimer) {
            System.out.println("Attempting to refresh");

            // Try to get a Billboard from the Server
            Response response;
            try {
                connector.start();
                response = connector.sendRequest(Request.viewScheduledBillboardReq());
                connector.close();
                currentBillboard = (Billboard) response.getData();
            }
            // If we could not get a Billboard because of connectivity issues, no matter the reason, display an error
            catch (IOException ex) {
                currentBillboard = Billboard.errorBillboard();
            }

            // -----------------------------------------------------------------------------------------
            // Set up the display as appropriate

            // If there's already a billboard in place, clear it to avoid unnecessarily filling memory
            if (displayedBillboard != null) this.getContentPane().remove(displayedBillboard);

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
