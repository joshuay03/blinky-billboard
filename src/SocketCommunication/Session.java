package SocketCommunication;

import Exceptions.AuthenticationFailedException;
import Exceptions.NoSuchUserException;
import Server.Token;
import Server.Server;
import Server.User;
import Server.AuthenticationHandler;
import java.io.Serializable;

public class Session implements Serializable {
    public byte[] token;
    // While derivable from the token, this variable exists so that the client knows the username it's connected as.
    // The server will ignore this and always derive the username from the session token
    public String username;
    public boolean canCreateBillboards;
    public boolean editAllBillboards;
    public boolean scheduleBillboards;
    public boolean editUsers;

    public Session(Credentials credentials, Server server) throws AuthenticationFailedException, NoSuchUserException {
        // The session should only be successfully created if Authentication succeeds
        if (!AuthenticationHandler.Authenticate(credentials, server.database))
        throw new AuthenticationFailedException(credentials.getUsername());
        else{
            User serverUser = new User(credentials.getUsername(), server.database);
            this.token = Token.Generate(credentials.getUsername());
            this.username = serverUser.getCredentials().getUsername();
            this.canCreateBillboards = serverUser.CanCreateBillboards;
            this.editAllBillboards = serverUser.EditAllBillBoards;
            this.scheduleBillboards = serverUser.ScheduleBillboards;
            this.editUsers = serverUser.EditUsers;
        }
    }
}
