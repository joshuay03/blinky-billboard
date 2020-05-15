package ControlPanel;

import SocketCommunication.*;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ControlPanel extends SocketConnection implements SocketCommunication, Runnable {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private JFrame frame;

    public ControlPanel(String propFile) {
        super(propFile);
        frame = new JFrame("Control Panel");
        frame.setPreferredSize(new Dimension(1000, 500));
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    }

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

    public static void main(String[] args) {
        ControlPanel controlPanel = new ControlPanel("/Users/joshuayoung/IdeaProjects/BlinkyBillboard/properties.txt");
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
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        frame.setContentPane(new Login(frame).loginPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * A method to communicate with another socket and send a message.
     *
     * @param msg a string containing the output being sent to another socket.
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
     * // A method to communicate with another socket and retrieve a message.
     *
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