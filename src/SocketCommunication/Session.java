package SocketCommunication;

import Exceptions.AuthenticationFailedException;
import Exceptions.NoSuchUserException;
import Server.AuthenticationHandler;
import Server.Token;
import Server.User;
import Server.blinkyDB;

import java.io.Serializable;

/**
 * Class for defining a user's session in the control panel.
 *
 * Implements the Serializable class.
 * @see Serializable
 */
public class Session implements Serializable {
    public byte[] token;
    // While derivable from the token, this variable exists so that the client knows the username it's connected as.
    // The server will ignore this and always derive the username from the session token
    public String username;
    public User serverUser;
    public boolean canCreateBillboards;
    public boolean editAllBillboards;
    public boolean scheduleBillboards;
    public boolean editUsers;

    /**
     * A constructor which instantiates a Session object. Generates a token for the
     * user using the credentials object. Sets the permissions of the user.
     *
     * @param credentials stores the user's username and hashed password
     * @see Credentials
     * @param database the blinkyDB database class which contains the SQL queries.
     * @see blinkyDB
     * @throws AuthenticationFailedException
     * @throws NoSuchUserException
     */
    public Session(Credentials credentials, blinkyDB database) throws AuthenticationFailedException, NoSuchUserException {
        // The session should only be successfully created if Authentication succeeds
        if (!AuthenticationHandler.Authenticate(credentials, database))
            throw new AuthenticationFailedException(credentials.getUsername());
        else {
            serverUser = new User(credentials.getUsername(), database);
            this.token = Token.Generate(credentials.getUsername());
            this.username = serverUser.getSaltedCredentials().getUsername();
            this.canCreateBillboards = serverUser.CanCreateBillboards();
            this.editAllBillboards = serverUser.CanEditAllBillboards();
            this.scheduleBillboards = serverUser.CanScheduleBillboards();
            this.editUsers = serverUser.CanEditUsers();
        }
    }
}
