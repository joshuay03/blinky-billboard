package Server;

import Exceptions.UserAlreadyExistsException;
import SocketCommunication.Credentials;
import SocketCommunication.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;


/**
 * A class to initiate a server for client-server connection.
 * Extends the SocketConnection class.
 *
 * @see SocketConnection
 */
public class Server extends SocketConnection {
    private ServerSocket server;
    private Socket client;
    private blinkyDB database;
    private boolean serverIsOpen;

    /**
     * Constructor for the server object. Calls the base class constructor.
     *
     * @param propFile a file containing the relevent networking information.
     */
    public Server(String propFile) {
        super(propFile);
    }

    public static void main(String[] args) {
        Server server = new Server("properties.txt");
        try {
            server.start();
            try { // Create root user credentials
                new User(new Credentials("Root", "root"), true, true, true, true, server.database);
            } catch (UserAlreadyExistsException ignored) {
            }
            server.database.CreateViewer("localhost:5506");

            server.serverIsOpen = server.isServerAliveUtil();
            System.out.println("Server Alive: " + server.serverIsOpen);

            if (server.serverIsOpen)
                System.out.println("Currently operating on port: " + server.getPort());
            else
                System.out.println("Try starting the server again...");

            while (server.serverIsOpen) {
                server.createClientThread(); // Wait for a client to connect to the server
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to initialise the server. Calls the retrievePort method and creates a new serversocket.
     */
    public void start() throws IOException {
        super.start();
        try {
            server = new ServerSocket(getPort());
            if (connectToDB(0) == false) { // Try connect to the database
                close();
            }
        } catch (Exception e) {
            System.out.println("The port " + getPort() + " is currently already in use.");
        }
    }

    /**
     * Method which tries instantiating a database connection.
     *
     * @param numTries
     * @return true/false value - whether the server could connect to the database
     */
    private boolean connectToDB(int numTries) {
        int tries = numTries; // Number of times tried connecting to the database
        try {
            this.database = new blinkyDB(); // create new database
        } catch (SQLException e) {
            System.out.println("Connection to database failed. Attempting connection again in 10 seconds.");
            if (tries < 2) {
                try {
                    Thread.sleep(10000); // Sleep for 10 seconds
                } catch (InterruptedException t) {
                }
                tries++;
                connectToDB(tries); // Try connecting again
            } else {
                System.out.println("The connection has been attempted multiple times. Closing the server.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("db.props file not found. Closing server.");
            return false;
        }
        return true;
    }

    /**
     * A helper method to determine whether or not the server is currently active.
     *
     * @return a boolean value representing whether or not the server can be connected to.
     */
    public boolean isServerAliveUtil() {
        boolean isAlive = false;
        String host = "localhost"; // pass host in
        try { // try connecting to the serversocket
            InetAddress ip = InetAddress.getByName(host);
            Socket testSocket = new Socket(ip, getPort());
            testSocket.close();
            isAlive = true;
        } catch (IOException e) { // Socket cannot connect to the server
            System.out.println(e + " - cannot connect to " + host + " on port " + getPort() + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAlive;
    }

    /**
     * A method to handle incoming socket requests and allocate a new thread independently
     * to each socket. Creates a new input and output stream, and a client and passes these objects
     * to the clientHandler object.
     *
     * @see ClientHandler
     */
    public void createClientThread() throws Exception {
        // socket object to receive incoming client requests
        client = server.accept();

        System.out.println("A new client is connected : " + client);

        // obtaining input and output streams
        DataInputStream input = new DataInputStream(client.getInputStream());
        DataOutputStream output = new DataOutputStream(client.getOutputStream());

        System.out.println("Assigning new thread for this client");
        // create a new thread object
        Thread thread = new ClientHandler(client, input, output, database);

        // Start the thread
        thread.start();
    }

    /**
     * Method to close the server.
     */
    public void close() throws IOException {
        server.close();
        super.close();
        serverIsOpen = false;
    }
}
