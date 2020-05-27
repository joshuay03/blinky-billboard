package Tests;

import Server.ClientHandler;
import Server.blinkyDB;
import SocketCommunication.Credentials;
import SocketCommunication.Request;
import SocketCommunication.Response;
import SocketCommunication.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.sql.SQLException;
import java.util.function.Function;

import static SocketCommunication.ServerRequest.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.*;

class FunctionalityTest {

    Session session;
    Function<Request, Response> respondTo;

    @BeforeEach @Test
    void setUpAndLogin() throws SQLException, IOException{
        respondTo = new ClientHandler(null, null, null, new blinkyDB())::handleInboundRequest;
        // Create and send a login request
        Credentials credentials = new Credentials("Liran", "SeaMonkey123");
        Request LoginRequest = new Request(LOGIN, credentials, null);
        Response res = respondTo.apply(LoginRequest);
        assertTrue(res.isStatus());
        if (res.isStatus()){
            // Set the session token
            session = (Session) res.getData();
        }
        else fail();
    }

    @Test
    void SendLogOut(){
        Request LogOutRequest = new Request(LOGOUT, null, session);

        Response res = respondTo.apply(LogOutRequest);

        assertTrue(res.isStatus());
    }
}
