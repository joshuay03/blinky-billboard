package Server;

import SocketCommunication.Credentials;

import java.util.Random;

public class User {
    public Credentials credentials;
    public byte[] salt;
    public boolean CanCreateBillboards;
    public boolean EditAllBillBoards;
    public boolean ScheduleBillboards;
    public boolean EditUsers;

    public User(Credentials credentials, boolean canCreateBillboards, boolean editAllBillBoards, boolean scheduleBillboards, boolean editUsers) {
        this.credentials = credentials;
        CanCreateBillboards = canCreateBillboards;
        EditAllBillBoards = editAllBillBoards;
        ScheduleBillboards = scheduleBillboards;
        EditUsers = editUsers;
        salt = new byte[100];
        new Random().nextBytes(salt);
    }
}
