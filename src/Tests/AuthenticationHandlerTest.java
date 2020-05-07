package Tests;

import Server.AuthenticationHandler;
import SocketCommunication.Credentials;
import org.junit.jupiter.api.Test;

import java.net.Authenticator;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationHandlerTest_Unsalted {

    @Test
    // Correct Username and Password
    void successfulAuthentication() {
        assertTrue(AuthenticationHandler.Authenticate(new Credentials("Liran", "SeaMonkey123")));
    }

    // Correct username, password does not match user's password
    void authenticateWithCorrectUsernameOnly() {
        assertFalse(AuthenticationHandler.Authenticate(new Credentials("Liran", "SeaMonkey12")));
    }

    // Incorrect username, password hash exists in database
    void authenticateWithIncorrectUsernameANDExistingHash(){
        assertFalse(AuthenticationHandler.Authenticate(new Credentials("Liraaan", "SeaMonkey123")));
    }

    // Non-existent username, password does not exist in database
    void authenticateWithNonExistingUserAndNonExistingPassword(){
        assertFalse(AuthenticationHandler.Authenticate(new Credentials("Liraaan", "SeaMonkey12")));
    }
}