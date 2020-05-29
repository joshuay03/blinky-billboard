package Client;

import SocketCommunication.SocketConnection;
import SocketCommunication.Session;
import SocketCommunication.Request;
import SocketCommunication.Response;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * A base class for handling client communication.
 * Extends the SocketConnection base class for intitialising a socket connection with the server.
 * Implements the SocketCommunication interface for communicating directly with the server.
 * @see SocketConnection
 */
public class ClientConnector extends SocketConnection {
    public Session session; // Starts empty, stays empty on Viewers
    Socket socket;
    DataInputStream input;
    DataOutputStream output;

    /**
     * Constructor for the server object. Calls the base class constructor.
     * @param propFile a file containing the relevent networking information.
     */
    public ClientConnector(String propFile) {
        super(propFile);
    }

    /**
     * Method to initialise the client connection.
     */
    public void start() throws IOException{
        super.start();
        port = Integer.toString(getPort());
        if (port == null) {
            close();
        }
        // If the address is not an IP, get the IP from the address
        InetAddress ip;
        boolean isIP = true;
        byte[] IPAddress = null;
        if (getIP().split("\\.").length == 4)
        {
            // Suspected IPv4 address
            try{
                // Convert the string into a byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Arrays.stream(getIP().split("\\.")).forEach((String part) -> bos.write(Integer.valueOf(part).byteValue()));
                IPAddress = bos.toByteArray();
            } catch (NumberFormatException e) {
                isIP = false;
            }
        }
        else if (getIP().split(":").length > 0)
        {
            // Suspected IPv6 address
            try{
                // Convert the string into a byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Arrays.stream(getIP().split(":")).forEach((String part) -> bos.write(Integer.valueOf(Integer.parseInt(part)).byteValue()));
                IPAddress = bos.toByteArray();
                if (IPAddress.length != getIP().split(":").length) throw new NumberFormatException(); // If some of the values were over
            }
            catch (NumberFormatException e){
                isIP = false;
            }
        }
        if (isIP) {
            assert IPAddress != null;
            ip = InetAddress.getByAddress(IPAddress);
        }
        else ip = InetAddress.getByName("localhost");
        try {
            // establish the connection with server port - this must be updated through the properties file
            socket = new Socket(ip, getPort());
            // obtaining input and out streams
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            session = null;
            System.out.println("Connection initialised...");
        }
        catch(IOException e) {
            System.out.println("Could not initialise the connection...");
        }
    }

    public void close() throws IOException {
        super.close();
        socket.close();
    }

    /**
     * A function for clients to send requests to the server
     * @param req The request to send
     * @return The response from the server
     * @throws IOException If the server connection failed
     */
    public Response sendRequest(Request req) throws IOException {
        // Write the request to the server
        assert output != null;
        output.write(req.withSession(session).serialise());
        Response res = null;
        // Read the response from the server
        try {
            ObjectInputStream ois = new ObjectInputStream(input);
            res = (Response)ois.readObject();
            System.out.println("Response: " + res);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assert res != null;
        // Return the response that was received
        return res;
    }
}
