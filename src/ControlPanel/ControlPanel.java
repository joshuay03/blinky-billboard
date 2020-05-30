package ControlPanel;

import Client.ClientConnector;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * A class which represents a control panel which connects to the server
 */
public class ControlPanel extends ClientConnector implements Runnable {
    protected ClientConnector connector;
    protected Socket socket;
    protected DataInputStream input;
    protected DataOutputStream output;
    protected JFrame frame;

    /**
     * Constructs a control panel which uses the information in the properties file to establish a connection with the
     * server
     * @param propFile the properties file which contains the IP and Port of the server
     */
    public ControlPanel(String propFile) {
        super(propFile);
        frame = new JFrame("Control Panel");
        frame.setPreferredSize(new Dimension(900, 500));
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    }

    /**
     * Initialises a socket connection
     * @throws IOException If the socket cannot start
     */
    public void start() throws IOException {
        super.start();

        try {
            socket = new Socket(getIP(), getPort());
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        }
        catch(Exception e) {
            System.out.println("The port " + getPort() + " is currently already in use.");
        }
    }

    /**
     * Creates a new control control panel instance
     * @param args unused
     */
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        ControlPanel controlPanel = new ControlPanel
                ("properties.txt");
        Scanner scanner = new Scanner(System.in);
        boolean controlPanelOpen = true;

        try {
            controlPanel.start();
            SwingUtilities.invokeLater(controlPanel);

            while (controlPanelOpen) {
                String outputData = scanner.nextLine();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Places the login panel in the control panel's frame in a separate thread
     */
    @Override
    public void run() {
        connector = new ClientConnector("properties.txt");
        frame.setContentPane(new Login(frame, this).loginPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}