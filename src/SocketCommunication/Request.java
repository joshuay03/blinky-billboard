package SocketCommunication;

import BillboardSupport.Billboard;
import Client.ClientConnector;
import Server.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class Request implements Serializable {
    public ServerRequest requestType;
    private Session session; //can be null

    public Billboard getBillboard() {
        return billboard;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public int getBillboardID() {
        return billboardID;
    }

    public User getUser() {
        return user;
    }

    public String getUsername() {
        return username;
    }

    // Request transmission data
    Billboard billboard = null;
    Credentials credentials = null;
    int billboardID;
    User user = null;
    String username = null;



    public ServerRequest getRequestType() {
        return requestType;
    }

    public Session getSession() {
        return session;
    }

    /**
     * A method to generate a request that the server provide the currently scheduled billboard
     * @return Request object to be sent to the server
     */
    public static Request scheduledBillboardReq(){
        return new Request(ServerRequest.VIEWER_CURRENTLY_SCHEDULED, null);
    }

    /**
     * A method to generate a request for the server to authenticate a user and provide a valid session
     * @param loginCredentials The Credentials of the user attempting to log in
     * @return Request object to be sent to the server
     */
    public static Request loginReq(Credentials loginCredentials){
        Request loginReq = new Request(ServerRequest.LOGIN, null);
        loginReq.credentials = loginCredentials;
        return loginReq;
    }

    /**
     * A method to generate a request for the server to provide a list of all Billboards stored on the server
     * @param session A Session object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request listAllBillboardsReq(Session session){
        return new Request(ServerRequest.LIST_BILLBOARDS, session);
    }

    /**
     * A method to generate a request for the server to provide detailed information about a specific billboard
     * @param billboardID The ID of the Billboard to retrieve information about
     * @param session A Session object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request getBillboardInfoReq(int billboardID, Session session){
        Request infoReq = new Request(ServerRequest.GET_BILLBOARD_INFO, session);
        infoReq.billboardID = billboardID;
        return infoReq;
    }

    /**
     * A method to generate a request for the server to store a new Billboard in the Database
     * @param newBillboard A Billboard Object to be stored in the database
     * @param session A Session object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request createBillboardReq(Billboard newBillboard, Session session){
        Request createBillboardReq = new Request(ServerRequest.CREATE_BILLBOARD, session);
        createBillboardReq.billboard = newBillboard;
        return createBillboardReq;
    }

    /**
     * A method to generate a request for the server to edit a particular Billboard. Note well, this request will have
     * the effect of COMPLETELY REPLACING the billboard with matching billboardID. Therefore, you must ensure you send
     * a Billboard which reflects the intended result (and not merely the intended changes to the Billboard)
     * @param billboardID The ID of the Billboard to edit
     * @param changedBillboard The resulting Billboard after changing the Billboard
     * @param session A Session object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request editBillboardReq(int billboardID, Billboard changedBillboard, Session session){
        Request editReq = new Request(ServerRequest.EDIT_BILLBOARD, session);
        editReq.billboardID = billboardID;
        editReq.billboard = changedBillboard;
        return editReq;
    }

    /**
     * A method to generate a request for the server to delete a particular Billboard
     * @param billboardID The ID of the Billboard to be deleted
     * @param session A Session Object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request deleteBillboardReq(int billboardID, Session session){
        Request deleteReq = new Request(ServerRequest.DELETE_BILLBOARD, session);
        deleteReq.billboardID = billboardID;
        return deleteReq;
    }

    /**
     * A method to generate a request for the server to remove a Billboard's scheduling information
     * @param billboardID The ID of the Billboard to remove from the schedule
     * @param session A Session object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request removeScheduledBillboardReq(int billboardID, Session session){
        Request removeSchReq = new Request(ServerRequest.REMOVE_SCHEDULED, session);
        removeSchReq.billboardID = billboardID;
        return removeSchReq;
    }

    /**
     * A method to generate a request for the server to provide a list of all registered users
     * @param session A session object for an authenticated user
     * @return A request to be sent to the server
     */
    public static Request listUsersReq(Session session){
        return new Request(ServerRequest.LIST_USERS, session);
    }

    /**
     * A method to generate a request for the server to create a new user
     * @param newUser A User object with credentials and desired permissions configured
     * @param session A session object for an authenticated user
     * @return A request object to be sent to the server
     */
    public static Request createUserReq(User newUser, Session session){
        Request newUserReq = new Request(ServerRequest.CREATE_USER, session);
        newUserReq.user = newUser;
        return newUserReq;
    }

    /**
     * A method to generate a request for the server to provide a user's permissions
     * @param username The username of the user whose permissions are requested
     * @param session A Session object for an authenticated user
     * @return A request to be sent to the server
     */
    public Request getUserPermissionsReq(String username, Session session){
        Request permsReq = new Request(ServerRequest.GET_USER_PERMISSION, session);
        permsReq.username = username;
        return permsReq;
    }

    // FIXME - Permissions should be a separate class so we don't have to send a whole user object around with unnecessary content in it
    //
    /**
     * A method to generate a request for the server to modify the permissions of a particular user
     * @param user A user object which contains the
     * @param session Session object for an authenticated user
     * @return Request to be sent to the server
     */
    public Request setUserPermissionsReq(User user,  Session session){
        Request setPermsReq = new Request(ServerRequest.SET_USER_PERMISSION, session);
        setPermsReq.user = user;
        return setPermsReq;
    }


    /**
     * A method to generate a request for the server to change a user's password. N.B. Server needs to check whether the user actually exists
     * Possible to end up with a malformed Credentials Object
     * @param proposedCredentials The proposed credentials of the new user
     * @param session A Session object for an authenticated user
     * @return Request to be sent to the server
     */
    public Request setPasswordReq(Credentials proposedCredentials, Session session){
        Request setPassReq = new Request(ServerRequest.SET_USER_PASSWORD, session);
        setPassReq.credentials = proposedCredentials;
        return setPassReq;
    }

    /**
     * A method to generate a request for the server to delete a user, given their username
     * @param username The username of the user to be deleted
     * @param session A Session object for an authenticated user
     * @return Request to be sent to the server
     */
    public Request deleteUserReq(String username, Session session){
        Request delReq = new Request(ServerRequest.DELETE_USER, session);
        delReq.username = username;
        return delReq;
    }

    /**
     * A method to generate a request for the server to log out a user, AND INVALIDATE THE GIVEN SESSION
     * @param session The session to be cancelled
     * @return Request to be sent to the server
     */
    public Request logoutReq(Session session){
        return new Request(ServerRequest.LOGOUT, session);
    }

    private Request(ServerRequest requestType, Session session) {
        this.requestType = requestType;
        this.session = session;
    }

    /**
     * Returns a request with a session attached - the attachment should happen at the connector level
     * @param session The session to attach
     * @return A new request with an attached session
     */
    public Request withSession(Session session){
        this.session = session;
        return this;
    }

    /**
     * Sends the request to the server
     * @param connector The connection to the server to send the request through
     * @return The response from the server
     * @throws IOException If the connection fails
     */
    public Response Send(ClientConnector connector) throws IOException {
        return connector.sendRequest(this);
    }


    public byte[] serialise(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }


}

