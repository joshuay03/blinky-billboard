package Tests;

import Client.ClientConnector;
import Exceptions.AuthenticationFailedException;
import Exceptions.NoSuchUserException;
import Server.ClientHandler;
import Server.blinkyDB;
import SocketCommunication.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    ClientConnector connector;
    blinkyDB db;

    /*
        Init the socket server.
     */
    @BeforeAll
    public void initServer() {
        try {
            server = new ServerSocket(5057);
            server.accept();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        try {
            db = new blinkyDB();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            fail();
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
            connector = new ClientConnector("properties.txt");
            socketInput = new DataInputStream(socket.getInputStream());
            socketOutput = new DataOutputStream(socket.getOutputStream());
            clientHandler = new ClientHandler(socket, socketInput, socketOutput, db);
            connector.start();
        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Test that the client handler can handle a a simple request.
     */
    @Test
    public void receiveBasicInputFromClient() throws IOException, AuthenticationFailedException, NoSuchUserException {
        Request req = new Request(ServerRequest.LOGIN, null, new Session(new Credentials("Root", "root"), db));
        Response res = req.Send(connector);
        assertTrue(res.isStatus());
    }

    @AfterEach
    public void finishTest() throws IOException {
        connector.close();
    }

    /*
        Test that the thread's connection can be closed while the server is open.
     */
    @AfterAll @Test
    public void closeConnection() {
        try {
            server.close();
        } catch (IOException e) {
            fail();
        }
        boolean closed = clientHandler.closeConnection();
        assertTrue(closed);
    }
}
