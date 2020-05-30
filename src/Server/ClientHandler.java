package Server;

import BillboardSupport.Billboard;
import BillboardSupport.DummyBillboards;
import BillboardSupport.Schedule;
import ControlPanel.CreateBillboards;
import Exceptions.*;
import SocketCommunication.*;

import javax.xml.transform.Result;
import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Collator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static SocketCommunication.ServerRequest.LOGIN;
import static SocketCommunication.ServerRequest.VIEWER_CURRENTLY_SCHEDULED;


/**
 * A class to handle each client individually on an assigned thread.
 */
public class ClientHandler extends Thread {
    private final int BILLBOARD_NAME = 0,
            CREATOR = 1,
            BACKGROUND_COLOUR = 2,
            MESSAGE_COLOUR = 3,
            INFORMATION_COLOUR = 4,
            MESSAGE = 5,
            INFORMATION = 6,
            IMAGE = 7;
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
                    Request req = (Request) inputObject.readObject();
                    outputData = handleInboundRequest(req); // Handle the client's request and retrieve the response for that request
                    outputWriter.writeObject(outputData); // Replaced below statement with a generic object writer
                } catch (IllegalStateException e) {
                    outputWriter.writeObject(new Response(false, e.getMessage())); // Send a response to the client, informing it that an invalid request has been sent
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                closed = closeConnection();
                System.out.println("Connection closed");
            }
        }
    }

    /**
     * A function which takes a request and returns a response object, to be sent back
     *
     * @param req The request to handle
     * @return A response
     */
    public Response handleInboundRequest(Request req) throws SQLException {
        final Response permissionDeniedResponse = new Response(false, "Permission denied, please log out and log back in.");
        User authenticatedUser = null;
        List<ServerRequest> authlessRequests = Arrays.asList(LOGIN, VIEWER_CURRENTLY_SCHEDULED);
        if (!authlessRequests.contains(req.getRequestType())) // Verify the token before continuing, except for LOGIN requests
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
                if (now.after(sessionAuthentication.expiry) || database.IsTokenBlackListed(req.getSession().token))
                {
                    return new Response(false,"Token has expired.");
                }
            } catch (InvalidTokenException e) {
                return new Response(false, "Token verification failed.");
            }
        }

        Collator collator = Collator.getInstance(Locale.ENGLISH);

        // *************************************************************************************
        // LOGIC SWITCHING
        // *************************************************************************************
        //<editor-fold desc="REQUEST TYPE SWITCHING">
        switch (req.getRequestType()) {
            case VIEWER_CURRENTLY_SCHEDULED: {
                return new Response(true, DummyBillboards.messagePictureAndInformationBillboard());
            }
            case LOGIN: {
                Credentials credentials;
                try {
                    credentials = req.getCredentials();
                } catch (Exception e) {
                    return new Response(false, "Missing username or password");
                }
                try {
                    return new Response(true, new Session(credentials, database));
                } catch (AuthenticationFailedException | NoSuchUserException e) {
                    return new Response(false, "Cannot create session.");
                }
            }
            case LIST_BILLBOARDS: {
                Response res = null; // null needs to be replaced with the server.
                // logic to return list of billboards e.g. new Response(true, BillboardList());
                List<Billboard> billboardList;
                try {
                    billboardList = database.getBillboards();
                    return new Response(true, billboardList);
                } catch (SQLException e) {
                    return new Response(false, "There was an SQL error");
                }
            }
            case GET_BILLBOARD_INFO: {
                // this is triggered inside the BillboardList()); GUI
                // The control panel send the server the Billboard Name and valid session
                String billboardName = req.getBillboardName();
                try {
                    Billboard result = database.getBillboard(billboardName);
                    //server responds with billboards contents
                    return new Response(true, result);
                } catch (BillboardNotFoundException e) {
                    return new Response(false, "Could not find Billboard with that ID");
                }
            }
            case CREATE_BILLBOARD: {
                assert authenticatedUser != null;
                Billboard billboard;
                try {
                    billboard = req.getBillboard();
                } catch (Exception e) {
                    return new Response(false, "Invalid billboard object");
                }
                if (authenticatedUser.CanCreateBillboards()) {
                    // If the user is allowed to create billboards
                    try {
                        database.createBillboard(billboard, authenticatedUser.getSaltedCredentials().getUsername());
                        return new Response(true, "Billboard created successfully");
                    } catch (BillboardAlreadyExistsException e) {
                        // If there's already a billboard
                        // If the user can edit all billboards, or if they're the creator of the existing billboard
                        if(authenticatedUser.CanEditAllBillboards() || e.getBillboard().getCreator().equals(authenticatedUser.getSaltedCredentials().getUsername()))
                        {
                            try {
                                database.editBillboard(billboard.getBillboardName(), billboard.getBackgroundColour(), billboard.getMessageColour(), billboard.getInformationColour(), billboard.getMessage(), billboard.getInformation(), billboard.getImageData());
                            } catch (BillboardNotFoundException ignored) {}
                            return new Response(true, "Existing billboard was found and edited successfully.");
                        }
                        else return new Response(false, "There's already an existing billboard with that name, which you may not edit.");
                    }
                } else {
                    return permissionDeniedResponse;
                }
            }
            case EDIT_BILLBOARD:
            {
                assert authenticatedUser != null;
                Billboard modifiedBillboard = req.getBillboard();
                Billboard orig;
                try {
                    orig = database.getBillboard(req.getBillboardName());
                } catch (BillboardNotFoundException e) {
                    return new Response(false, e.getMessage());
                }
                // If the user is allowed to edit this billboard
                if (orig.getCreator().equals(authenticatedUser.getSaltedCredentials().getUsername()) ||
                authenticatedUser.CanEditAllBillboards())
                {
                    try {
                        database.editBillboard(req.getBillboardName(), modifiedBillboard.getBackgroundColour(), modifiedBillboard.getMessageColour(), modifiedBillboard.getInformationColour(), modifiedBillboard.getMessage(), modifiedBillboard.getInformation(), modifiedBillboard.getImageData());
                    } catch (BillboardNotFoundException e) {
                        return new Response(false, e.getMessage());
                    }
                    return new Response(true, String.format("Billboard %s was changed successfully", req.getBillboardName()));
                }
                else return permissionDeniedResponse;
            }
            case DELETE_BILLBOARD:
            {
                try {
                    assert authenticatedUser != null;
                    if (authenticatedUser.CanEditAllBillboards()) {
                        database.DeleteBillboard(req.getBillboardName());
                        return new Response(true, "The billboard has successfully been deleted.");
                    } else
                        return permissionDeniedResponse;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new Response(false, "Billboard does not exist.");
                }
            }
            case VIEW_SCHEDULED_BILLBOARDS: {
                // this request will only happen is user has 'Schedule Billboards' permission
                // should be triggered inside the ScheduleBillboards() GUI
                try {
                    assert authenticatedUser != null;

                    // client will send server a valid session
                    List<Schedule> allScheduledBillboards = database.getSchedules(Timestamp.valueOf(LocalDateTime.now()));

                    // if session token is valid server will respond with list of billboards that have been scheduled
                    // including billboardName, creator, time scheduled, and duration
                    return new Response(true, allScheduledBillboards);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return new Response(false, "No billboards currently scheduled");
            }
            case SCHEDULE_BILLBOARD: {
                assert authenticatedUser != null;
                try {
                    Schedule schedule = req.getSchedule();
                    // In minutes i.e int value of 60 represents the billboard being displayed every 60 minutes for x duration
                    int interval = schedule.repeatInterval;
                    Timestamp currTime = schedule.StartTime;
                    long milliseconds;
                    if (authenticatedUser.CanScheduleBillboards()) {
                        while (currTime.before(Timestamp.valueOf(LocalDateTime.now().plusWeeks(4)))) { // Since there's no end time, I'm hardcoding 4 weeks from now
                            database.ScheduleBillboard(schedule.billboardName, schedule);
                            milliseconds = currTime.getTime() + ((interval * 60) * 1000);
                            currTime.setTime(milliseconds);
                            schedule.StartTime = currTime;
                        }
                        return new Response(true, "The billboard has successfully been scheduled.");
                    } else {
                        return permissionDeniedResponse;
                    }
                }
                catch (SQLException e) {
                    return new Response(false, "Unable to schedule billboard");
                }
        }
            case REMOVE_SCHEDULED:
            {
                try {
                    assert authenticatedUser != null;
                    if (authenticatedUser.CanEditAllBillboards()) {
                        database.UnscheduleBillboard(req.getBillboardName());
                        return new Response(true, "Billboard has been removed from the schedule.");
                    } else {
                        return permissionDeniedResponse;
                    }
                } catch (SQLException e) { // Will catch if the billboard does not exist or is not scheduled.
                    return new Response(false, "Billboard lookup failed.");
                }
            }
            case LIST_USERS: {
                // request only happens if user has 'Edit Users' permission
                assert authenticatedUser != null;
                if (authenticatedUser.CanEditUsers()) {
                    // triggered inside EditUsers() GUI
                    try {
                        List<String> usernames = new ArrayList<>();
                        ResultSet rs = database.LookUpAllUserDetails();
                        while (rs.next()) {
                            usernames.add(rs.getString("user_name"));
                        }
                        return new Response(true, usernames);
                        // Server responds with list of usernames (CRA then says "and any other information your teams feels appropriate")
                    } catch (SQLException e) {
                        return new Response(false, "Lookup failed.");
                    }

                } else {
                    return permissionDeniedResponse;
                }
            }
            case CREATE_USER:
            {
                assert authenticatedUser != null;
                // check if session is valid e.g. expired, if not return failure and trigger relogin
                // request only happens if user has 'Edit Users' permission
                // triggered inside EditUsers() GUI
                if (authenticatedUser.CanEditUsers()) {
                    // Client will send server username, list of permissions, hashedPassword, and valid session token
                    // TODO - Fix spec compliance
                    User newUser = req.getUser();

                    // Check if a user with the same username exists
                    try {
                        ResultSet resultSet = database.LookUpUserDetails(newUser.getSaltedCredentials().getUsername());
                        if (resultSet.next())
                            return new Response(false, "User with that username already exists. Please try again");
                            // else Server will create user and send back acknowledgement of success
                        else {
                            if(newUser.getSaltedCredentials().getUsername().length() < 1)
                                return new Response(false,"Username cannot be empty.");
                            if (newUser.getSaltedCredentials().getUsername().length() > 100)
                                return new Response(false, "Usernames must be up to 100 characters long");
                            database.RegisterUserInDatabase(newUser.getSaltedCredentials(),
                                    newUser.CanCreateBillboards(),
                                    newUser.CanEditAllBillboards(),
                                    newUser.CanScheduleBillboards(),
                                    newUser.CanEditUsers());

                            return new Response(true, "User successfully created!");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                // If they do not have permissions to do edit users, reject the request out of hand
                else {
                    return permissionDeniedResponse;
                }


            }
            case GET_USER_PERMISSION: {
                assert authenticatedUser != null;
                // check if session is valid e.g. expired, if not return failure and trigger relogin
                // TODO - implement

                // Client will send server a username and valid session token
                String queryUsername = req.getUsername();

                // Check that the user actually exists, and if they do, have the information on hand
                User existingUser = null;
                try {
                    existingUser = new User(queryUsername, database);
                } catch (NoSuchUserException e) {
                    e.printStackTrace();
                }

                // if session user is requesting their own details return details, no permissions required
                // if session user is requesting details of another user, check permissions = 'Edit Users' == true then return details
                if (req.getSession().username.equals(queryUsername) || authenticatedUser.CanEditUsers()) {
                    return new Response(true, existingUser);
                } else return permissionDeniedResponse;

                // else return false send error
            }
            case SET_USER_PERMISSION: {
                assert authenticatedUser != null;
                // request only happens if user has 'Edit Users' permission
                // triggered inside EditUsers() GUI
                if (authenticatedUser.CanEditUsers()) {

                    // if username does not exist return error

                    // else if session user is requesting to remove their own "Edit User" permission return error

                    // else Server change that users permissions and send back acknowledgement of success

                    //FIXME - should only be passing credentials object through on this one
                    try {
                        // Client will send server username(user whose permissions are to be changed),
                        // list of permissions, and valid session token
                        User userToModify = new User(req.getUsername(), database);

                        boolean canEditUsers = userToModify.CanEditUsers();
                        boolean canEditAllBillboards = userToModify.CanEditAllBillboards();
                        boolean canScheduleBillboards = userToModify.CanScheduleBillboards();
                        boolean canCreateBillboards = userToModify.CanCreateBillboards();

                        userToModify.setEditAllBillBoards(canEditAllBillboards);
                        userToModify.setScheduleBillboards(canScheduleBillboards);
                        userToModify.setCanCreateBillboards(canCreateBillboards);

                        // Special case - can't remove own admin permissions
                        // FIXME - need to implement a nuanced response which explains that the rest of the perms changes were successful
                        if (collator.compare(authenticatedUser.getSaltedCredentials().getUsername(), req.getUsername()) != 0) {
                            userToModify.setEditUsers(canEditUsers);
                        }

                        database.UpdateUserDetails(userToModify);

                    } catch (NoSuchUserException e) {
                        e.printStackTrace();
                    }
                } else return permissionDeniedResponse;

                // Client will send server username(user whose permissions are to be changed),
                // list of permissions, and valid session token

                // if username does not exist return error

                // else if session user is requesting to remove their own "Edit User" permission return error

                // else Server change that users permissions and send back acknowledgement of success
            }
                break;
            case SET_USER_PASSWORD:
            {
                assert authenticatedUser != null;
                // TODO - implement in GUI

                //If the user has the edit users permission, or if they are just trying to change their own password,
                // they may....
                if (authenticatedUser.CanEditUsers() || collator.compare(authenticatedUser.getSaltedCredentials().getUsername(), req.getUsername()) == 0) {

                    User userToChange;

                    try {
                        userToChange = new User(req.getUsername(), database);
                    } catch (NoSuchUserException e) {
                        return new Response(false, "Could not find user");
                    }

                    userToChange.setPasswordFromCredentials(userToChange.getSaltedCredentials(), database);
                    database.UpdateUserDetails(userToChange);

                } // else return false send error
                else return permissionDeniedResponse;


            }
            case DELETE_USER: {
                assert authenticatedUser != null;
                // check if session is valid e.g. expired, if not return failure and trigger relogin

                // request only happens if user has 'Edit Users' permission
                // triggered inside EditUsers() GUI
                if (authenticatedUser.CanEditUsers()) {

                    // Client will send username of user to be deleted and valid session token
                    String deletionCandidate = req.getUsername();
                    // if username != to username of session user (no user can delete themselves)
                    // Server will delete the user and send back acknowledgement of success
                    collator = Collator.getInstance(Locale.ENGLISH);
                    if (collator.compare(req.getSession().serverUser.getSaltedCredentials().getUsername(), deletionCandidate) == 0) {
                        return new Response(false, "User cannot delete their own account");
                    } else {
                        try {
                            database.DeleteUser(deletionCandidate);
                            return new Response(true, "User " + deletionCandidate + "successfully delete");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            // TODO - respond if no user exists
                        }
                    }


                    // TODO if username deleted = creator of a billboard, billboard will no longer have owner registered in DB
                    // NEED EDIT BILLBOARD
                    // CRA says for team to decide what will happen in this circumstance

                } else return new Response(false, permissionDeniedResponse);
            }
                break;
            case LOGOUT:
            {
                // Client will send server valid session token
                byte[] TokenToExpire = req.getSession().token;
                try {
                    // server will expire session token and send back and acknowledgement
                    database.BlacklistToken(TokenToExpire);
                    return new Response(true, "Log out successful");
                } catch (SQLException e) {
                    return new Response(false, "There was an SQL error");
                }
            }
        }
        // If the request is invalid:
        return new Response(false, String.format("%s is not a valid request type", req.getRequestType()));
        //throw new IllegalStateException("Invalid request type: " + req.getRequestType());
    }

    //</editor-fold>

    public boolean closeConnection() {
        boolean closed = false; // For testing purposes only
        try {
            System.out.println("The connection to the client is being closed.");
            client.close();
            input.close();
            output.close();
            closed = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return closed;
    }
}
