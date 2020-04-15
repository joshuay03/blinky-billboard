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
    private String propFile;
    private int port;
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
     * Method to initialise the client connection. Calls the retrievePort method.
     * @see #retrievePort()
     */
    public void start() {
        port = retrievePort();
        port = 5056; // Override the port number for now
        try {
            // ip of the local host
            InetAddress ip = InetAddress.getByName("localhost");
            // establish the connection with server port - this must be updated through the properties file
            socket = new Socket(ip, port);
            // obtaining input and out streams
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    /**
     * A helper method to get and return the current port which
     * the server is currently operating on.
     * @return the port which the server is operating on.
     */
    public int getPort() {
        return port;
    }

    /**
     * Retrieve the port number from the given properties file.
     * @return the port number.
     */
    private int retrievePort() {
        Integer port_number = null;
        try {
            port_number = 5057; // server is listening on port 5057 - must use the properties file to change the port
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            if (port_number == null) {
                return port_number; // "Was not able to abstract the port number from the given properties file."
            }
            return port_number;
        }
    }

    public boolean sendOutput(String msg) throws Exception {
        output.writeUTF(msg);

        if (msg.equalsIgnoreCase("exit")) {
            System.out.println("Closing this connection : " + socket);
            closeConnection();
            System.out.println("Connection closed");
            return true;
        }
        return false;
    }

    public String retrieveInput() throws Exception {
        String received = input.readUTF();
        return received;
    }

    public void closeConnection () throws IOException {
        socket.close();
    }
}
