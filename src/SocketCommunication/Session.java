package SocketCommunication;

import Exceptions.AuthenticationFailedException;
import Exceptions.NoSuchUserException;
import Server.Token;
import Server.Server;
import Server.User;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
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
        try {
            this.token = Token.Generate(credentials.getUsername());
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        this.username = serverUser.getCredentials().getUsername();
        this.canCreateBillboards = serverUser.CanCreateBillboards;
        this.editAllBillboards = serverUser.EditAllBillBoards;
        this.scheduleBillboards = serverUser.ScheduleBillboards;
        this.editUsers = serverUser.EditUsers;
    }
}
