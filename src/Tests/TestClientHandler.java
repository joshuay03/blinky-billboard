package Tests;

import Server.ClientHandler;
import org.junit.jupiter.api.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TestClientHandler {
    /*
        Declare necessary objects and properties.
     */
    DataInputStream input;
    DataOutputStream output;
    Socket client;
    ClientHandler clientHandler;

    /*
        Init the clientHandler object.
     */
    @BeforeAll
    public void initClientHandlerObject() {
        try {
            client = new Socket();
            input = new DataInputStream(client.getInputStream());
            output = new DataOutputStream(client.getOutputStream());
            clientHandler = new ClientHandler(client, input, output);
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    /*
        Test sending basic output to the client.
     */
    @Test
    public void sendBasicOutput() {
        clientHandler.sendOutput();
    }

    /*
        Test retrieving basic input from the client.
     */
    @Test
    public void retrieveBasicInput() {
        clientHandler.retrieveInput();
    }

    /*
        Test closing the threads connection to the server.
     */
    @Test
    public void closeThreadConnectionToServer() {
        clientHandler.closeConnection();
    }
}
