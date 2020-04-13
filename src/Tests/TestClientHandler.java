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

    /*
        Init the clientHandler object.
     */
    @BeforeAll
    public void initClientHandlerObject() {
        try {
            Socket client = new Socket();
            DataInputStream input = new DataInputStream(client.getInputStream());
            DataOutputStream output = new DataOutputStream(client.getOutputStream());
            ClientHandler clientHandler = new ClientHandler(client, input, output);
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
}
