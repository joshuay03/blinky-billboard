package ControlPanel;

import Client.ClientConnector;
import SocketCommunication.*;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * A class which represents a control panel which connects to the server
 * @author Joshua Young
 */
public class ControlPanel extends ClientConnector implements SocketCommunication, Runnable {
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
     * @throws IOException
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
     * @param args
     */
    public static void main(String[] args) {
        ControlPanel controlPanel = new ControlPanel
                (System.getProperty("user.dir") + "/properties.txt");
        Scanner scanner = new Scanner(System.in);
        boolean controlPanelOpen = true;

        try {
            controlPanel.start();
            SwingUtilities.invokeLater(controlPanel);

            while (controlPanelOpen) {
                String outputData = scanner.nextLine();

                if (outputData.equalsIgnoreCase("exit")) {
                    controlPanel.sendOutput(outputData);
                    controlPanel.close();
                    controlPanelOpen = false;
                }
                else {
                    controlPanel.sendOutput(outputData);
                    controlPanel.retrieveInput();
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
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

    /**
     * Communicates with and sends a message to the server
     *
     * @param msg a string containing the output being sent to the server
     */
    @Override
    public void sendOutput(String msg) {
        try{
            output.writeUTF(msg);
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Communicates with and receives a message from the server
     */
    @Override
    public void retrieveInput() {
        try {
            System.out.println(input.readUTF());
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
}