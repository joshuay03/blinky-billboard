package SocketCommunication;

import Exceptions.AuthenticationFailedException;
import Exceptions.NoSuchUserException;
import Server.Token;
import Server.blinkyDB;
import Server.User;
import Server.AuthenticationHandler;
import java.io.Serializable;

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
