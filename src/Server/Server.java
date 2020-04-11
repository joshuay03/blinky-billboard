package Server;

import java.io.*;
import java.net.*;

/**
 * @author Nick
 * @version 0
 * A class containing elements to initiate a server for client-server connection.
 */
public class Server {
    private Integer port;
    private ServerSocket server;
    private Socket client;

    /**
     * Constructor for the server object. Calls the retrievePort method and creates a new serversocket.
     * @param propsFile a file containing the relevent networking information.
     * @see #retrievePort(String)
     */
    public Server(String propsFile) {
        port = retrievePort(propsFile);
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
    public Integer getPort() {
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
    private Integer retrievePort(String propsFile) {
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
}
