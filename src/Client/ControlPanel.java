package Client;

import SocketCommunication.SocketCommunication;
import SocketCommunication.SocketConnection;

import java.io.IOException;
import java.net.Socket;

public class ControlPanel extends SocketConnection implements SocketCommunication {
    private Socket socket;

    public ControlPanel(String propFile) {
        super(propFile);
    }

    public void start() throws IOException {
        super.start();
        try {
            socket = new Socket(getIP(), getPort());
        }
        catch(Exception e) {
            System.out.println("The port " + getPort() + " is currently already in use.");
        }
    }

    @Override
    public void sendOutput(String msg) {

    }

    @Override
    public String retrieveInput() {
        return null;
    }

    public static void main(String args[]){
        ControlPanel controlPanel = new ControlPanel("C:\\Users\\Nick\\Desktop\\BlinkyBillboard\\src\\Server\\properties.txt");
        boolean controlPanelOpen = true;
        try {
            controlPanel.start();

            while (true) {
                if (controlPanelOpen) {
                    continue;
                }
                else {
                    controlPanel.close();
                    controlPanelOpen = false;
                }
            }
        }
        catch (Exception e) {

            System.out.println(e);
        }
    }
}

