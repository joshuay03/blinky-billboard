package Tests;

import Client.ClientConnection;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestClientConnection {
    ClientConnection clientConnection;
    String propFile = "t";

    @BeforeAll
    public void initClientConnectionObject() {
        clientConnection = new ClientConnection(propFile);
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
    public void sendBasicOutput() {

    }

    /*
        Test retrieving a response from the server.
     */
    @Test
    public void retrieveBasicInput() {

    }

    /*
        Test closing the client connection.
     */
    @Test
    public void closeClientConnection() {

    }
}
