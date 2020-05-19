package Server;

import Exceptions.AuthenticationFailedException;
import Exceptions.NoSuchUserException;
import SocketCommunication.SocketCommunication;
import SocketCommunication.Request;
import SocketCommunication.Session;
import SocketCommunication.Response;
import SocketCommunication.Credentials;


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
        Response<?> outputData;
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
    public Response handleInboundRequest(Request req) throws IOException {
        Session session;

        // Example handle login
        switch(req.getRequestType()) {
            case VIEWER_CURRENTLY_SCHEDULED:
                break;
            case LOGIN:
                // EXAMPLE how to use the request given from the client
                String username = req.getData().get("username");
                String password = req.getData().get("password");

                if (username == null || password == null) {
                    // failure status and error message
                    return new Response(false, "Missing username or password");

                }

                Credentials credentials = new Credentials(username, password);
                try {
                    // User the real server for the parameter not null
                    session = new Session(credentials, null);
                } catch (AuthenticationFailedException | NoSuchUserException e) {
                    return new Response( false, "Cannot create session.");
                }

                return new Response(true, session);

            case LIST_BILLBOARD:
                session = req.getSession();
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // logic to return list of billboards e.g. new Response(true, BillboardList());

                break;
            case GET_BILLBOARD_INFO:
                // this is triggered inside the BillboardList()); GUI
                // The control panel send the server the Billboard Name and valid session
                // token e.g session = req.getSession();

                //server responds with billboards contents

                break;
            case CREATE_BILLBOARD:
                // triggered inside CreateBillboards() GUI
                // Client sends the Server the billboards name, contents (billboard ID, creator) and valid session token
                // something like String billboardName = req.getData().get(billboardName);

                // get list of billboards session user has created
                // if billboardName in list make edit if not return error

                // if billboardName does not exist create new billboard
                // if billboardName exist and is currently scheduled edit can not be made return error
                // if billboardName exist and is not currently scheduled replace contents of billboard with new contents

                break;
            case EDIT_BILLBOARD:
                // this request will only happen if User has 'Edit all Billboards' permission
                // Client sends server billboard name, contents (billboard ID, creator) and valid session token
                // Edit can be made by this user to any billboard on list (even if currently scheduled)
                // if edit is made replace contents of billboard with new

                break;
            case DELETE_BILLBOARD:
                // Client sends server billboard name and valid session token
                // Do something to find or check the perrmissions of session if not already found

                // if session.permissions = 'Create Billboards' get list of billboards session user has created
                // if billboard is created by session user and not currently scheduled allow delete else return error

                // if session.permissions = 'Edit All Billboards' allow delete on any billboard

                break;
            case VIEW_SCHEDULED_BILLBOARD:
                // this request will only happen is user has 'Schedule Billboards' permission
                // I think it should be triggered inside the ScheduleBillboards() GUI

                // client will send server a valid session
                // if session token is valid server will respond with list of billboards the have been scheduled
                // including billboardName, creator, time scheduled, and duration

                break;
            case SCHEDULE_BILLBOARD:
                // this request will only happen is user has 'Schedule Billboards' permission
                // triggered inside the ScheduleBillboards() GUI

                //Client will send server billboardName, timeScheduled, duration, and valid session
                // (there also might be more information e.g for handling recurrence)

                // Server will then respond by adding new billboard to schedule
                // if there is a billboard previously scheduled for the time of the newly scheduled billboard
                // new billboard will take precedence over previously scheduled billboard but will not delete previously scheduled

                // e.g billboard A scheduled from 10:00-11:00 and billboard B is then scheduled from 10:30-11
                // billboard A will display until 10:30 then billboard B will be displayed

                // once billboard is scheduled Server will send back an acknowledgement of success

                break;
            case REMOVE_SCHEDULED:
                // this request will only happen is user has 'Schedule Billboards' permission
                // triggered inside the ScheduleBillboards() GUI

                //Client will send the valid session token of the user along with billboardName and scheduledTime
                // of billboard that is to be deleted

                // Server will respond by removing the billboard from the schedule and sending back an acknowledgement of success

                break;
            case LIST_USERS:
                // request only happens if user has 'Edit Users' permission
                // triggered inside EditUsers() GUI

                // Client sends server valid session token
                // Server responds with list of usernames (CRA then says "and any other information your teams feels appropriate")

                break;
            case CREATE_USER:

                break;
            case GET_USER_PERMISSION:
                break;
            case SET_USER_PERMISSION:
                break;
            case SET_USER_PASSWORD:
                break;
            case DELETE_USER:
                break;
            case LOGOUT:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + req.getRequestType());
        }


//        String inputData = input.readUTF();
//        System.out.println(this.client + " request: " + inputData); // Print client request to server
//        if (inputData.equalsIgnoreCase("exit"))
//            closeConnection();
//        return inputData; // Will query the database with the input and return the response into "output" variable
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
