package Server;

import SocketCommunication.SocketCommunication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * A class to handle each client individually on an assigned thread.
 * Implements the SocketCommunication interface for communicating directly with the client socket.
 * @see SocketCommunication
 */
public class ClientHandler extends Thread implements SocketCommunication {
    private DataInputStream input;
    private DataOutputStream output;
    private Socket client;


    public ClientHandler(Socket client, DataInputStream input, DataOutputStream output) {
        this.client = client;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        String outputData;
        while (true) {
            try {
                outputData = handleInboundRequests(); // Handle the client's request and retrieve the response for that request
                output.writeUTF("Request: " + outputData + " yielded the response: " + outputData); // Write a message to the client.
            }
            catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public String handleInboundRequests() throws IOException {
        String inputData = input.readUTF();
        System.out.println(this.client + " request: " + inputData); // Print client request to server
        if (inputData.equalsIgnoreCase("exit"))
            closeConnection();
        return inputData; // Will query the database with the input and return the response into "output" variable
    }

    public boolean closeConnection() {
        boolean closed = false; // For testing purposes only
        try {
            System.out.println("The connection to the client is being closed.");
            client.close();
            input.close();
            output.close();
            closed = true;
        }
        catch(IOException e) {
            System.out.println(e);
        }
        return closed;
    }

    @Override
    public void sendOutput(String msg) {

    }

    @Override
    public String retrieveInput() {
        return null;
    }
}
