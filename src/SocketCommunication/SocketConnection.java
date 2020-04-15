package SocketCommunication;

import java.io.IOException;

/**
 * A base class for initialising a socket connection.
 */
public class SocketConnection {
    protected String port;
    protected String propFile;

    protected SocketConnection(String propFile) {
        this.propFile = propFile;
    }

    /**
     * A helper method to get and return the current port which
     * the socket is currently operating on.
     * @return the port which the socket is operating on as in integer value.
     */
    public int getPort() {
        return Integer.parseInt(port);
    }

    /**
     * Retrieve the port number from the given properties file.
     * @throws IOException
     */
    protected void retrievePort() throws IOException {
        propFile = "t"; // Do something with prop file.
        port = null; // initially port is equal to null, if no port is found in prop file, remains null.
        port = "5056"; // server is listening on port 5056 - must use the properties file to change the port
        if (port == null) {
            System.out.println("Was not able to obtain the port number from the given properties file.");
        }
    }

    /**
     * A method to initialise the socket connection.
     * Will most likely be overriden as functionality for this method is dependent
     * on what type of socket is used and how it will be used.
     * @throws IOException
     */
    protected void start() throws IOException {
        System.out.println("The connection has been started...");
    }

    /**
     * A method to close the socket connection.
     * Will most likely be overriden as functionality for this method is dependent
     * on what type of socket is used and how it will be used.
     * @throws IOException
     */
    protected void close() throws IOException {
        System.out.println("The connection has been closed...");
    }
}
