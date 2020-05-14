package SocketCommunication;

import Exceptions.AuthenticationFailedException;
import Exceptions.NoSuchUserException;
import Server.AuthenticationHandler;
import Server.Server;
import Server.User;
import Server.blinkyDB;

import java.io.Serializable;

public class Session implements Serializable {
    public byte[] token;
    public String username;
    public boolean canCreateBillboards;
    public boolean editAllBillboards;
    public boolean scheduleBillboards;
    public boolean editUsers;

    public Session(Credentials credentials, Server server) throws AuthenticationFailedException, NoSuchUserException {
        User serverUser = new User(credentials.getUsername(), server.database);
        this.token = server.addToken(credentials); // Try adding a token to the server based on the credentials that were put in
        // Authentication succeeded
        this.username = serverUser.getCredentials().getUsername();
        this.canCreateBillboards = serverUser.CanCreateBillboards;
        this.editAllBillboards = serverUser.EditAllBillBoards;
        this.scheduleBillboards = serverUser.ScheduleBillboards;
        this.editUsers = serverUser.EditUsers;
    }
}
