package Server;

import BillboardSupport.Billboard;
import BillboardSupport.DummyBillboards;
import Exceptions.AuthenticationFailedException;
import Exceptions.InvalidTokenException;
import Exceptions.NoSuchUserException;
import SocketCommunication.*;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static SocketCommunication.ServerRequest.LOGIN;
import static SocketCommunication.ServerRequest.VIEWER_CURRENTLY_SCHEDULED;

/**
 * A class to handle each client individually on an assigned thread.
 * Implements the SocketCommunication interface for communicating directly with the client socket.
 * @see SocketCommunication
 */
public class ClientHandler extends Thread implements SocketCommunication {
    private DataInputStream input;
    private DataOutputStream output;
    private Socket client;
    private blinkyDB database;

    public ClientHandler(Socket client, DataInputStream input, DataOutputStream output, blinkyDB database) {
        this.client = client;
        this.input = input;
        this.output = output;
        this.database = database;
    }

    @Override
    public void run() {
        Response outputData;
        boolean closed = false;
        while (!closed) {
            try {
                ObjectInputStream inputObject = new ObjectInputStream(input);
                ObjectOutputStream outputWriter = new ObjectOutputStream(output);
                try {
                    // Cast the data into a request object to find out what the client wants
                    Request req = (Request)inputObject.readObject();
                    outputData = handleInboundRequest(req); // Handle the client's request and retrieve the response for that request
                    outputWriter.writeObject(outputData); // Replaced below statement with a generic object writer
                }
                catch (IllegalStateException e){
                    outputWriter.writeObject(new Response(false, e.getMessage())); // Send a response to the client, informing it that an invalid request has been sent
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
     * A function which takes a request and returns a response object, to be sent back
     * @param req The request to handle
     * @return A response
     */
    public Response handleInboundRequest(Request req) {
        final Response permissionDeniedResponse = new Response(false, "Permission denied, please log out and log back in.");
        User authenticatedUser = null;
        List<ServerRequest> authlessRequests = Arrays.asList(LOGIN, VIEWER_CURRENTLY_SCHEDULED);
        if(!authlessRequests.contains(req.getRequestType())) // Verify the token before continuing, except for LOGIN requests
        {
            try {
                Token sessionAuthentication = Token.validate(req.getSession().token);
                try {
                    authenticatedUser = new User(sessionAuthentication.username, database);
                } catch (NoSuchUserException e) {
                    return new Response(false, "The user this token was assigned to is not registered.");
                }
                // Get current timestamp
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                // Check if the token is expired
                if (now.after(sessionAuthentication.expiryDate))
                {
                    return new Response(false,"Token has expired.");
                }
            } catch (InvalidTokenException e) {
                return new Response(false, "Token verification failed.");
            }
        }
        // Example handle login
        switch(req.getRequestType()) {
            case VIEWER_CURRENTLY_SCHEDULED:
            {
                return new Response(true, DummyBillboards.messagePictureAndInformationBillboard());
            }
            case LOGIN:
            {
                // EXAMPLE how to use the request given from the client
                Credentials credentials;
                try{
                    credentials = (Credentials)req.getData();
                }catch (Exception e)
                {return new Response(false, "Missing username or password");}
                try {
                    return new Response(true, new Session(credentials, database));
                } catch (AuthenticationFailedException | NoSuchUserException e) {
                    return new Response( false, "Cannot create session.");
                }
            }
            case LIST_BILLBOARDS:
            {
                class BillboardList {
                    List<Billboard> billboardList;
                    public BillboardList(List<Billboard> billboardList) {
                        this.billboardList = billboardList;
                    }
                }
                Response res = null; // null needs to be replaced with the server.
                // logic to return list of billboards e.g. new Response(true, BillboardList());
                List<Billboard> billboardList = new ArrayList<>();
                try{
                    ResultSet rs = database.getBillboards();
                    while (rs.next()){
                        // For each returned billboard from the database
                        Object image;
                        try{
                            ByteArrayInputStream bis = new ByteArrayInputStream(rs.getBytes("billboardImage"));
                            ObjectInput in = new ObjectInputStream(bis);
                            image = in.readObject();
                        }
                        catch (Exception e){
                            image = null;
                        }
                        Billboard current = new Billboard();
                        current.setBillboardDatabaseKey(rs.getInt("billboard_id"));
                        current.setCreator(rs.getString("creator"));
                        current.setBackgroundColour(new Color(rs.getInt("backgroundColour")));
                        current.setMessageColour(new Color(rs.getInt("messageColour")));
                        current.setInformationColour(new Color(rs.getInt("informationColour")));
                        current.setMessage(rs.getString("message"));
                        current.setInformation(rs.getString("information"));
                        current.setImageData((String) image);
                        billboardList.add(current);
                    }
                } catch (SQLException e){
                    return new Response(false, "There was an SQL error");
                }
                // The billboard list now has all of the returned billboards - convert to an array and return
                return new Response(true, billboardList.toArray(new Billboard[0]));
            }
            case GET_BILLBOARD_INFO:
                // check if session is valid e.g. expired, if not return failure and trigger relogin - already done above
                // this is triggered inside the BillboardList()); GUI
                // The control panel send the server the Billboard Name and valid session
                // token e.g session = req.getSession();

                //server responds with billboards contents


                break;
            case CREATE_BILLBOARD:
            {
                assert authenticatedUser != null;
                Billboard billboard;
                try{
                    billboard = (Billboard) req.getData();
                }catch (Exception e)

                {return new Response(false, "Invalid billboard object"); }

                if(authenticatedUser.CanCreateBillboards())
                {

                } else return permissionDeniedResponse;

                // triggered inside CreateBillboards() GUI
                // user with "Create Billboards" permission // inside Gui



                // Client sends the Server the billboards name, contents (billboard ID, creator) and valid session token
                // something like String billboardName = req.getData().get(billboardName);
                String billboardInfo = billboard.getInformation();



                // if billboard info searched is not valid e.g corresponding billboardName, id, and creator nonexistent or incorrect send back error

                // if billboard does exist
                // get list of billboards session user has created
                // if billboardName in list make edit if not return error

                // if billboardName does not exist create new billboard

                // if billboardName exist and is currently scheduled edit can not be made return error
                // if billboardName exist and is not currently scheduled replace contents of billboard with new contents
            }
                break;
            case EDIT_BILLBOARD:
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // this request will only happen if User has 'Edit all Billboards' permission
                // Client sends server billboardName, contents (billboard ID, creator) and valid session token

                // if billboard info searched is not valid e.g corresponding billboardName, id, and creator nonexistent or incorrect send back error

                // Edit can be made by this user to any billboard on list (even if currently scheduled)
                // if edit is made replace contents of billboard with new

                break;
            case DELETE_BILLBOARD:
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // Client sends server billboard name and valid session token

                // if billboardName does not exist return error

                // Do something to find or check the permissions of session if not already found

                // if session.permissions = 'Create Billboards' get list of billboards session user has created
                // if billboard is created by session user and not currently scheduled allow delete else return error

                // if session.permissions = 'Edit All Billboards' allow delete on any billboard

                break;
            case VIEW_SCHEDULED_BILLBOARD:
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // this request will only happen is user has 'Schedule Billboards' permission
                // should be triggered inside the ScheduleBillboards() GUI

                // client will send server a valid session

                // if session token is valid server will respond with list of billboards that have been scheduled
                // including billboardName, creator, time scheduled, and duration

                break;
            case SCHEDULE_BILLBOARD:
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // this request will only happen is user has 'Schedule Billboards' permission
                // triggered inside the ScheduleBillboards() GUI

                //Client will send server billboardName, timeScheduled, duration, and valid session
                // (there also might be more information e.g for handling recurrence)

                // if billboard name is not found return error

                // Else server will add new billboard to schedule

                // if there is a billboard previously scheduled for the time of the newly scheduled billboard
                // new billboard will take precedence over previously scheduled billboard but will not delete previously scheduled

                // e.g billboard A scheduled from 10:00-11:00 and billboard B is then scheduled from 10:30-11
                // billboard A will display until 10:30 then billboard B will be displayed

                // once billboard is scheduled Server will send back an acknowledgement of success

                break;
            case REMOVE_SCHEDULED:
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // this request will only happen is user has 'Schedule Billboards' permission
                // triggered inside the ScheduleBillboards() GUI

                //Client will send the valid session token, billboardName and scheduledTime of billboard that is to be deleted

                // if billboardName does not exist or is not scheduled for sheduledTime return error

                // if info is correct Server will remove the billboard from the schedule and send back an acknowledgement of success

                break;
            case LIST_USERS:
            {
                // request only happens if user has 'Edit Users' permission
                assert authenticatedUser != null;
                if (authenticatedUser.CanEditUsers()){
                    // triggered inside EditUsers() GUI
                    try {
                        List<String> usernames = new ArrayList<>();
                        ResultSet rs = database.LookUpAllUserDetails();
                        while (rs.next())
                        {
                            usernames.add(rs.getString("user_name"));
                        }
                        return new Response(true, usernames);
                        // Server responds with list of usernames (CRA then says "and any other information your teams feels appropriate")
                    } catch (SQLException e) {
                        return new Response(false, "Lookup failed.");
                    }

                }
                else
                {
                    return permissionDeniedResponse;
                }
            }
            case CREATE_USER:
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // request only happens if user has 'Edit Users' permission
                // triggered inside EditUsers() GUI

                // Client will send server username, list of permissions, hashedPassword, and valid session token

                // if username already exist send error

                // else Server will create user and send back acknowledgement of success

                break;
            case GET_USER_PERMISSION:
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // Client will send server a username and valid session token

                // if session user is requesting their own details return details, no permissions required

                // if session user is requesting details of another user, check permissions = 'Edit Users' == true then return details

                // else return false send error

                break;
            case SET_USER_PERMISSION:
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // request only happens if user has 'Edit Users' permission
                // triggered inside EditUsers() GUI

                // Client will send server username(user whose permissions are to be changed),
                // list of permissions, and valid session token

                // if username does not exist return error

                // else if session user is requesting to remove their own "Edit User" permission return error

                // else Server change that users permissions and send back acknowledgement of success

                break;
            case SET_USER_PASSWORD:
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // Client will send server a username and hashedPassword

                // if session user is trying to change own password Server will change password and send back acknowledgement of success

                // else if session user is trying to change password of another user check permission = 'Edit User' == true then allow change
                //and send back acknowledgement of success

                // else return false send error
                break;
            case DELETE_USER:
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // request only happens if user has 'Edit Users' permission
                // triggered inside EditUsers() GUI

                // Client will send username of user to be deleted and valid session token

                // if username != to username of session user (no user can delete themselves)
                // Server will delete the user and send back acknowledgement of success

                // if username deleted = creator of a billboard, billboard will no longer have owner registered in DB
                // CRA says for team to decide what will happen in this circumstance

                break;
            case LOGOUT:
                // Client will send server valid session token

                // server will expire session token and send back and acknowledgement
                break;
        }
        // If the request is invalid:
        return new Response(false, String.format("%s is not a valid request type", req.getRequestType()));
        //throw new IllegalStateException("Invalid request type: " + req.getRequestType());
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
