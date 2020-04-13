package Server;

import IConnectable.Connectible;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * @author Nick
 * @version 1
 * A class to handle each client individually on an assigned thread.
 */
public class ClientHandler extends Thread implements Connectible {
    private DataInputStream input;
    private DataOutputStream output;
    private Socket client;

    public ClientHandler(Socket client, DataInputStream input, DataOutputStream output) {
        this.client = client;
        this.input = input;
        this. output = output;
    }

    @Override
    public void sendOutput() {

    }

    @Override
    public void retrieveInput() {

    }

    @Override
    public void closeConnection() {

    }
}
