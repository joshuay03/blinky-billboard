package Client;

import SocketCommunication.SocketCommunication;
import SocketCommunication.SocketConnection;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ControlPanel extends SocketConnection implements SocketCommunication {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public ControlPanel(String propFile) {
        super(propFile);
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

    @Override
    public void sendOutput(String msg) {
        try{
            output.writeUTF(msg);
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void retrieveInput() {
        try {
            System.out.println(input.readUTF());
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String args[]){
        ControlPanel controlPanel = new ControlPanel("C:\\Users\\Nick\\Desktop\\BlinkyBillboard\\src\\Server\\properties.txt");
        Scanner scanner = new Scanner(System.in);

        boolean controlPanelOpen = true;
        try {
            controlPanel.start();

            while (controlPanelOpen) {
                String outputData = scanner.nextLine();
                if ( outputData.equalsIgnoreCase("exit")) {
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
}

