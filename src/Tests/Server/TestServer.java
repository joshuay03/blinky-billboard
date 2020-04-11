package Tests.Server;

import Server.Server;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

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
    public void SetupServer() {
        server = new Server(propsFile);
    }

    /*
        Test 2: Retrieve the port from the properties file.
     */
    @Test
    public void retrievePort() {
        assertEquals(5056, server.getPort());
    }

    /*
        Test 4: Check that a socket can connect to the server while the server is open.
     */
    @Test
    public void setupRequestsCheckServerIsAliveTrue() {
        assertEquals(true, server.isServerAliveUtil());
    }

    /*
        Test 3: Check that a socket cannot connect to the server while the server is closed.
     */
    @Test
    public void setupRequestsCheckServerIsAliveFalse() {
        server.close();
        assertEquals(false, server.isServerAliveUtil());
    }
}