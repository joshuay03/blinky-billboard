package Server;

import java.io.*;
import java.net.*;

/**
 * @author Nick
 * @version 3
 * A class containing elements to initiate a server for client-server connection.
 */
public class Server {
    private int port;
    private ServerSocket server;
    private Socket client;
    private String propFile;

    /**
     * Constructor for the server object.
     * @param propFile a file containing the relevent networking information.
     * @see #retrievePort(String)
     */
    public Server(String propFile) {
        this.propFile = propFile;
    }

    /**
     * Method to initialise the server. Calls the retrievePort method and creates a new serversocket.
     */
    public void start() {
        port = retrievePort(propFile);
        port = 5056; // Override the port number for now
        try {
            server = new ServerSocket(port);
        }
        catch(Exception e) {
            System.out.println("The port " + port + " is currently already in use.");
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
     * A helper method to determine whether or not the server is currently active.
     * @return a boolean value representing whether or not the server can be connected to.
     */
    public boolean isServerAliveUtil() {
        boolean isAlive = false;
        String host = "localhost"; // pass host in
        try{
            InetAddress ip = InetAddress.getByName(host);
            Socket testSocket = new Socket(ip, port);
            testSocket.close();
            isAlive = true;
        }
        catch(Exception e) {
            System.out.println(e + " - cannot connect to " + host + " on port " + port + ".");
        }
        return isAlive;
    }

    /**
     * Retrieve the port number from the given properties file.
     * @param propsFile
     * @return the port number.
     */
    private int retrievePort(String propsFile) { // will be an interface as the clients will use this method too
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

    /**
     * A method to handle incoming socket requests and allocate a new thread indipendently
     * to each socket. Creates new input and output streams, and a client and passes these objects
     * to the clientHandler object.
     * @see ClientHandler
     * @throws IOException
     */
    public void createClientThread() {
        try {
            // socket object to receive incoming client requests
            client = server.accept();

            System.out.println("A new client is connected : " + client);

            // obtaining input and output streams
            DataInputStream input = new DataInputStream(client.getInputStream());
            DataOutputStream output = new DataOutputStream(client.getOutputStream());

            System.out.println("Assigning new thread for this client");
            // create a new thread object
            Thread thread = new ClientHandler(client, input, output);

            // Start the thread
            thread.start();
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Method to close the server.
     */
    public void close() {
        try {
            server.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String args[]) {
        Server server = new Server("t");
        boolean serverOpen = true;
        server.start();
        while (true) {
            if (serverOpen) {
                server.createClientThread();
            }
            else
                server.close();
        }
    }
}
