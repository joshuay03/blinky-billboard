package Tests;

import Server.ClientHandler;
import SocketCommunication.Request;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestClientHandler {
    /*
        Declare necessary objects and properties.
     */
    DataInputStream socketInput, clientInput;
    DataOutputStream socketOutput, clientOutput;
    Socket socket, client;
    ServerSocket server;
    ClientHandler clientHandler;

    /*
        Init the socket server.
     */
    @BeforeAll
    public void initServer() {
        try {
            server = new ServerSocket(5057);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Init the client handler object.
     */
    @BeforeEach
    public void initThread() {
        try {
            client = new Socket("localhost", 5057);
            clientInput = new DataInputStream(client.getInputStream());
            clientOutput = new DataOutputStream(client.getOutputStream());
            socket = server.accept();
            socketInput = new DataInputStream(socket.getInputStream());
            socketOutput = new DataOutputStream(socket.getOutputStream());
            clientHandler = new ClientHandler(socket, socketInput, socketOutput);
        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Test that the client handler can handle a a simple request.
     */
    @Test
    public void receiveBasicInputFromClient() throws IOException {
        clientOutput.writeUTF("hello");
        Request req = new Request();
        String msg = clientHandler.handleInboundRequest(req);
        assertEquals("hello", msg);
    }

    /*
        Test that the thread's connection can be closed while the server is open.
     */
    @AfterAll @Test
    public void closeConnection() {
        boolean closed = clientHandler.closeConnection();
        assertTrue(closed);
    }
}
