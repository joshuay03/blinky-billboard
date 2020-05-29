package SocketCommunication;

import java.io.*;

/**
 * A base class for initialising a socket connection.
 */
public class SocketConnection {
    protected String port;
    protected String propFile;
    protected String IP;

    protected SocketConnection(String propFile) {
        this.propFile = propFile;
    }

    /**
     * A helper method to get and return the current port which
     * the socket is currently operating on.
     *
     * @return the port which the socket is operating on as in integer value.
     */
    public int getPort() {
        return Integer.parseInt(port);
    }

    public String getIP() {
        return IP;
    }

    /**
     * Retrieve the port number from the given properties file.
     *
     * @throws IOException
     */
    private void retrievePort() throws IOException {
        try {
            File file = new File(propFile);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String[] arr;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("port:")) {
                    arr = line.split("port:", 0);
                    port = arr[1].trim();
                } else if (line.startsWith("IP:")) {
                    arr = line.split("IP:", 0);
                    IP = arr[1].trim();
                }
            }
            if (port == null) {
                System.out.println("Was not able to obtain the port number from the given properties file.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("The properties file you have entered does not exist.");
        }
    }

    /**
     * A method to initialise the socket connection.
     * Will most likely be overriden as functionality for this method is dependent
     * on what type of socket is used and how it will be used.
     *
     * @throws IOException
     */
    public void start() throws IOException {
        retrievePort();
        System.out.println("Initialising connection...");
    }

    /**
     * A method to close the socket connection.
     * Will most likely be overriden as functionality for this method is dependent
     * on what type of socket is used and how it will be used.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        System.out.println("The connection has been closed...");
    }
}
