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
    @BeforeAll @Test
    public void SetupServer() throws IOException {
        server = new Server(propsFile);
    }

    /*
        Test 2: Retrieve the port from the properties file.
     */
    @Test
    public void retrievePort() {
        assertEquals(5057, server.getPort());
    }

    /*
        Test 3: Start the server.
     */
    @Test
    public void setupRequests() throws IOException {
        assertEquals(true, server.isServerAliveUtil());
    }
}