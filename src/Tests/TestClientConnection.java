package Tests;

import Client.ClientCommunication;

import org.junit.jupiter.api.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestClientConnection {
    ClientCommunication clientConnection;
    ServerSocket server;
    Socket socket;
    String propFile = "t";
    DataInputStream input;

    @BeforeAll
    public void initClientConnectionObject() {
        try {
            server = new ServerSocket(5056);
            clientConnection = new SocketCommunication(propFile);
            socket = server.accept();
            input = new DataInputStream(socket.getInputStream());
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    @BeforeEach
    public void startConnection() {
        clientConnection.start();
    }

    /*
        Test retrieving the port from the properties file.
     */
    @Test
    public void retrievePort() {
        assertEquals(5056, clientConnection.getPort());
    }

    /*
        Test sending output to the server.
     */
    @Test
    public void sendBasicOutput() throws IOException {
        String msg = "hello";
        clientConnection.sendOutput(msg);
        assertEquals(input.readUTF(), msg);
    }

    /*
        Test retrieving a response from the server.
     */
    @Test
    public void retrieveBasicInput() {
        String val = clientConnection.retrieveInput();
        assertEquals("hello", val);
    }

    /*
        Test closing the client connection.
     */
    @Test
    public void closeClientConnection() {
        boolean val = clientConnection.closeConnection();
        assertEquals(true, val);
    }
}
