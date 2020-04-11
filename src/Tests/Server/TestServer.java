package Tests.Server;

import Server.Server;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestServer {
    /*
        Test 0: Declaring Server object and necessary properties.
    */
    Server server;
    String propsFile = "T";

    /*
           Test 1: Constructing a Server object.
        */
    @BeforeAll
    public void setupServerObject() {
        server = new Server(propsFile);
    }

    @BeforeEach
    public void startServer() {
        server.start();
    }

    /*
        Test 2: Retrieve the port from the properties file.
     */
    @Test
    public void retrievePort() {
        assertEquals(5056, server.getPort());
    }

    /*
        Test 3: Check that a socket can connect to the server while the server is open.
     */
    @Test
    public void createClientCheckServerIsAliveTrue() {
        assertEquals(true, server.isServerAliveUtil());
    }

    /*
        Test 4: Check that a socket cannot connect to the server while the server is closed.
     */
    @Test
    public void createClientCheckServerIsAliveFalse() {
        server.close();
        assertEquals(false, server.isServerAliveUtil());
    }

    /*
        Test 5: Check that multiple clients can connect to the server.
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