package Server;

import Exceptions.AuthenticationFailedException;
import SocketCommunication.Credentials;
import SocketCommunication.SocketConnection;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

/**
 * A class to initiate a server for client-server connection.
 * Extends the FileParse class.
 * @see SocketConnection
 */
public class Server extends SocketConnection {
    private ServerSocket server;
    private Socket client;
    private List<Token> tokens; // Currently valid tokens
    public blinkyDB database;

    /**
     * Constructor for the server object. Calls the base class constructor.
     * @param propFile a file containing the relevent networking information.
     */
    public Server(String propFile) {
        super(propFile);
        try {
            this.database = new blinkyDB();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("db.props file not found.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Connection to database failed.");
        }
    }

    /**
     * Method to initialise the server. Calls the retrievePort method and creates a new serversocket.
     */
    public void start() throws IOException {
        super.start();
        try {
            server = new ServerSocket(getPort());
        }
        catch(Exception e) {
            System.out.println("The port " + getPort() + " is currently already in use.");
        }
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
            Socket testSocket = new Socket(ip, getPort());
            testSocket.close();
            isAlive = true;
        }
        catch(Exception e) {
            System.out.println(e + " - cannot connect to " + host + " on port " + getPort() + ".");
        }
        return isAlive;
    }

    /**
     * A method to handle incoming socket requests and allocate a new thread indipendently
     * to each socket. Creates a new input and output stream, and a client and passes these objects
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

    public byte[] addToken(Credentials credentials) throws AuthenticationFailedException {
        if(AuthenticationHandler.Authenticate(credentials, database))
        {
            // Placeholder token generator - for now it's just gonna be random.
            byte[] newToken = new byte[100];
            new Random().nextBytes(newToken);
            tokens.add(new Token(newToken));
            return newToken;
        }
        else
        {
            throw new AuthenticationFailedException(credentials.getUsername());
        }
    }

    /**
     * Method which takes credentials and creates an internal User object and adds it to the list of connected users
     */
    public void formUserInstance(Credentials credentials) {
        //User user = new User();

        //users.add(user);
    }
    /**
     * Method to close the server.
     */
    public void close() throws IOException{
        server.close();
        super.close();
    }

    public static void main(String[] args){
        Server server = new Server("db.props");
        try {
            server.start();
            boolean serverOpen = server.isServerAliveUtil();
            System.out.println("Server Alive: " + serverOpen);
            System.out.println("Currently operating on port: " + server.getPort());

                while (true) {
                if (serverOpen) {
                    server.createClientThread();
                }
                else {
                    server.close();
                    serverOpen = false;
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
