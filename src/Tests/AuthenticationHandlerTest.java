package Tests;

import Exceptions.UserAlreadyExistsException;
import Server.AuthenticationHandler;
import Server.User;
import Server.blinkyDB;
import SocketCommunication.Credentials;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationHandlerTest {
    private static blinkyDB database;

    @BeforeAll
    static void initialiseDBAndSetUser() throws IOException, SQLException {
        database = new blinkyDB();
        try {
            new User(new Credentials("Liran", "SeaMonkey123"), false, false, false, false, database);
        } catch (UserAlreadyExistsException ignored) {}
    }

    @Test
    // Correct Username and Password
    void successfulAuthentication() {
        assertTrue(AuthenticationHandler.Authenticate(new Credentials("Liran", "SeaMonkey123"), database));
    }

    @Test
    // Correct username, password does not match user's password
    void authenticateWithCorrectUsernameOnly() {
        assertFalse(AuthenticationHandler.Authenticate(new Credentials("Liran", "SeaMonkey12"), database));
    }

    @Test
    // Incorrect username, password hash exists in database
    void authenticateWithIncorrectUsernameANDExistingHash() {
        assertFalse(AuthenticationHandler.Authenticate(new Credentials("Liraaan", "SeaMonkey123"), database));
    }

    @Test
    // Non-existent username, password does not exist in database
    void authenticateWithNonExistingUserAndNonExistingPassword() {
        assertFalse(AuthenticationHandler.Authenticate(new Credentials("Liraaan", "SeaMonkey12"), database));
    }
}