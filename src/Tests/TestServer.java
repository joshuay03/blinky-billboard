package Tests;

import Server.Server;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestServer {
    /*
        Declare Server object and necessary properties.
    */
    Server server;
    String propsFile = "T";

    /*
           Construct a Server object.
        */
    @BeforeAll
    public void setupServerObject() {
        server = new Server(propsFile);
    }

    /*
        Start the server.
     */
    @BeforeEach
    public void startServer() {
        server.start();
    }

    /*
        Retrieve the port from the properties file.
     */
    @Test
    public void retrievePort() {
        assertEquals(5056, server.getPort());
    }

    /*
        Check that a socket can connect to the server while the server is open.
     */
    @Test
    public void createClientCheckServerIsAliveTrue() {
        assertEquals(true, server.isServerAliveUtil());
    }

    /*
        Check that a socket cannot connect to the server while the server is closed.
     */
    @Test
    public void createClientCheckServerIsAliveFalse() {
        server.close();
        assertEquals(false, server.isServerAliveUtil());
    }

    /*
        Check that multiple clients can connect to the server.
     */
    @Test void createClientHandleMultipleConnections() throws IOException {
        Socket socket;
        int count = 0;
        int expected = 2;
        for(int i = 0; i < 2; i++) {
            socket = new Socket("localhost", server.getPort());
            server.createClientThread();
            count++;
        }
        assertEquals(expected, count);
    }
}