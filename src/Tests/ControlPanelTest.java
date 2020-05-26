package Tests;

import SocketCommunication.SocketCommunication;
import SocketCommunication.SocketConnection;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ControlPanelTest extends SocketConnection implements SocketCommunication {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public ControlPanelTest(String propFile) {
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
            e.printStackTrace();
        }
    }

    @Override
    public void retrieveInput() {
        try {
            System.out.println(input.readUTF());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        ControlPanelTest controlPanelTest = new ControlPanelTest("C:\\Users\\Nick\\Desktop\\BlinkyBillboard\\src\\Server\\properties.txt");
        Scanner scanner = new Scanner(System.in);

        boolean controlPanelOpen = true;
        try {
            controlPanelTest.start();

            while (controlPanelOpen) {
                String outputData = scanner.nextLine();
                if ( outputData.equalsIgnoreCase("exit")) {
                    controlPanelTest.sendOutput(outputData);
                    controlPanelTest.close();
                    controlPanelOpen = false;
                }
                else {
                    controlPanelTest.sendOutput(outputData);
                    controlPanelTest.retrieveInput();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
