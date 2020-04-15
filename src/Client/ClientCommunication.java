package Client;

import SocketCommunication.SocketCommunication;
import SocketCommunication.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * A base class for handling client communication.
 * Extends the SocketConnection base class for intitialising a socket connection with the server.
 * Implements the SocketCommunication interface for communicating directly with the server.
 * @see SocketCommunication
 * @see SocketConnection
 */
public class ClientCommunication extends SocketConnection implements SocketCommunication {
    Socket socket;
    DataInputStream input;
    DataOutputStream output;

    /**
     * Constructor for the server object. Calls the base class constructor.
     * @param propFile a file containing the relevent networking information.
     */
    public ClientCommunication(String propFile) {
        super(propFile);
    }

    /**
     * Method to initialise the client connection.
     */
    public void start() throws IOException{
        super.start();
        port = "5056"; // Override the port number for now
        // ip of the local host
        InetAddress ip = InetAddress.getByName("localhost");
        // establish the connection with server port - this must be updated through the properties file
        socket = new Socket(ip, getPort());
        // obtaining input and out streams
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    public void close() throws IOException {
        super.close();
        socket.close();
    }

    @Override
    public void sendOutput(String msg) {

    }

    @Override
    public String retrieveInput() {
        return null;
    }
}
