package Server;

import SocketCommunication.SocketCommunication;
import SocketCommunication.Request;

import java.io.*;
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
        boolean closed = false;
        while (!closed) {
            try {
                ObjectInputStream inputObject = new ObjectInputStream(input);
                ObjectOutputStream outputWriter = new ObjectOutputStream(output);
                try {
                    // Cast the data into a request object to find out what the client wants
                    Request req = (Request)inputObject.readObject();
                    outputData = handleInboundRequest(req); // Handle the client's request and retrieve the response for that request
                    outputWriter.writeObject("Request: " + outputData + " yielded the response: " + outputData); // Replaced below statement with a generic object writer
                    //output.writeUTF("Request: " + outputData + " yielded the response: " + outputData); // Write a message to the client.
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            catch (IOException e) {
                closed = closeConnection();
                System.out.println("Connection closed");
            }
        }
    }

    /**
     * A function which takes requests and returns a response object, to be sent back Todo: Rewrite the function to do something other than returning a string
     * @param req The request to handle
     * @return A response Todo: Replace the string with a response object
     * @throws IOException Won't be thrown once the function gets rewritten to handle objects
     */
    public String handleInboundRequest(Request req) throws IOException {
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
            e.printStackTrace();
        }
        return closed;
    }

    @Override
    public void sendOutput(String msg) {

    }

    @Override
    public void retrieveInput() {

    }
}
