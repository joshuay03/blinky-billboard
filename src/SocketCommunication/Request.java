package SocketCommunication;

import BillboardSupport.Billboard;
import BillboardSupport.Schedule;
import Client.ClientConnector;
import Server.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class implements a series of methods, representing the different
 * types of requests which can be made to the server. Each method returns
 * a Request object based off of the request type.
 * Implements Serializable.
 */
public class Request implements Serializable {
    public ServerRequest requestType;
    // Request transmission data
    Billboard billboard = null;
    Credentials credentials = null;
    String billboardName;
    User user = null;
    String username = null;
    private Session session; //can be null
    private Schedule schedule = null;

    private Request(ServerRequest requestType, Session session) {
        this.requestType = requestType;
        this.session = session;
    }

    /**
     * A method to generate a request that the server provide the currently scheduled billboard
     *
     * @return Request object to be sent to the server
     */
    public static Request viewScheduledBillboardReq() {
        return new Request(ServerRequest.VIEWER_CURRENTLY_SCHEDULED, null);
    }

    /**
     * A method to generate a request that the server provide the currently scheduled billboard
     *
     * @return Request object to be sent to the server
     */
    public static Request viewCurrentlyScheduledBillboardReq(Session session) {
        return new Request(ServerRequest.VIEW_SCHEDULED_BILLBOARDS, session);
    }

    /**
     * @return
     */
    public static Request scheduleBillboardReq(Schedule schedule, Session session) {
        Request request = new Request(ServerRequest.SCHEDULE_BILLBOARD, session);
        request.schedule = schedule;
        return request;
    }

    /**
     * A method to generate a request for the server to authenticate a user and provide a valid session
     *
     * @param loginCredentials The Credentials of the user attempting to log in
     * @return Request object to be sent to the server
     */
    public static Request loginReq(Credentials loginCredentials) {
        Request loginReq = new Request(ServerRequest.LOGIN, null);
        loginReq.credentials = loginCredentials;
        return loginReq;
    }

    /**
     * A method to generate a request for the server to provide a list of all Billboards stored on the server
     *
     * @param session A Session object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request listAllBillboardsReq(Session session) {
        return new Request(ServerRequest.LIST_BILLBOARDS, session);
    }

    /**
     * A method to generate a request for the server to provide detailed information about a specific billboard
     *
     * @param billboardName The name of the Billboard to retrieve information about
     * @param session     A Session object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request getBillboardInfoReq(String billboardName, Session session) {
        Request infoReq = new Request(ServerRequest.GET_BILLBOARD_INFO, session);
        infoReq.billboardName = billboardName;
        return infoReq;
    }

    /**
     * A method to generate a request for the server to store a new Billboard in the Database
     *
     * @param newBillboard A Billboard Object to be stored in the database
     * @param session      A Session object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request createBillboardReq(Billboard newBillboard, Session session) {
        Request createBillboardReq = new Request(ServerRequest.CREATE_BILLBOARD, session);
        createBillboardReq.billboard = newBillboard;
        return createBillboardReq;
    }

    /**
     * A method to generate a request for the server to edit a particular Billboard. ALSO NOTE - this method can be used to schedule Billboards
     * <p>
     * Note well, this request will have
     * the effect of COMPLETELY REPLACING the billboard with matching billboardName. Therefore, you must ensure you send
     * a Billboard which reflects the intended result (and not merely the intended changes to the Billboard)
     *
     * @param billboardName    The name of the Billboard to edit
     * @param changedBillboard The resulting Billboard after changing the Billboard
     * @param session          A Session object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request editBillboardReq(String billboardName, Billboard changedBillboard, Session session) {
        Request editReq = new Request(ServerRequest.EDIT_BILLBOARD, session);
        editReq.billboardName = billboardName;
        editReq.billboard = changedBillboard;
        return editReq;
    }

    /**
     * A method to generate a request for the server to delete a particular Billboard
     *
     * @param billboardName The name of the Billboard to be deleted
     * @param session       A Session Object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request deleteBillboardReq(String billboardName, Session session) {
        Request deleteReq = new Request(ServerRequest.DELETE_BILLBOARD, session);
        deleteReq.billboardName = billboardName;
        return deleteReq;
    }

    /**
     * A method to generate a request for the server to remove a Billboard's scheduling information
     *
     * @param billboardName The name of the Billboard to remove from the schedule
     * @param session     A Session object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request removeScheduledBillboardReq(String billboardName, Session session) {
        Request removeSchReq = new Request(ServerRequest.REMOVE_SCHEDULED, session);
        removeSchReq.billboardName = billboardName;
        return removeSchReq;
    }

    /**
     * A method to generate a request for the server to add scheduling information to a Billboard
     *
     * @param schedule The billboard, including scheduling information
     * @param session                            A Session object for an authenticated user
     * @return Request object to be sent to the server
     */
    public static Request scheduleBillboard(Schedule schedule, Session session) {
        Request schedReq = new Request(ServerRequest.SCHEDULE_BILLBOARD, session);
        schedReq.schedule = schedule;
        return schedReq;
    }

    /**
     * A method to generate a request for the server to provide a list of all registered users
     *
     * @param session A session object for an authenticated user
     * @return A request to be sent to the server
     */
    public static Request listUsersReq(Session session) {
        return new Request(ServerRequest.LIST_USERS, session);
    }

    /**
     * Method for creating a new user.
     * @param credentials The credentials belonging to the user.
     * @param CreateBillboards true/false permission
     * @param ScheduleBillboards true/false permission
     * @param EditAllBillboards true/false permission
     * @param EditUsers true/false permission
     * @param session The session belongin to the user.
     * @return A request to be sent to the server.
     */
    public static Request createUserReq(Credentials credentials, boolean CreateBillboards, boolean ScheduleBillboards, boolean EditAllBillboards, boolean EditUsers, Session session) {
        Request newUserReq = new Request(ServerRequest.CREATE_USER, session);
        newUserReq.user = new User(credentials, CreateBillboards, EditAllBillboards, ScheduleBillboards, EditUsers);
        return newUserReq;
    }

    /**
     * Method for editing a new user.
     * @param credentials The credentials belonging to the user.
     * @param CreateBillboards true/false permission
     * @param ScheduleBillboards true/false permission
     * @param EditAllBillboards true/false permission
     * @param EditUsers true/false permission
     * @param session The session belongin to the user.
     * @return A request to be sent to the server.
     */
    public static Request editUserReq(Credentials credentials, boolean CreateBillboards, boolean ScheduleBillboards, boolean EditAllBillboards, boolean EditUsers, Session session) {
        Request newUserReq = new Request(ServerRequest.EDIT_USER, session);
        newUserReq.user = new User(credentials, CreateBillboards, EditAllBillboards, ScheduleBillboards, EditUsers);
        return newUserReq;
    }

    /**
     * A method to generate a request for the server to provide a user's permissions
     *
     * @param username The username of the user whose permissions are requested
     * @param session  A Session object for an authenticated user
     * @return A request to be sent to the server
     */
    public static Request getUserPermissionsReq(String username, Session session) {
        Request permsReq = new Request(ServerRequest.GET_USER_PERMISSION, session);
        permsReq.username = username;
        return permsReq;
    }

    /**
     * A method to generate a request for the server to modify the permissions of a particular user
     *
     * @param user    A user object which contains the
     * @param session Session object for an authenticated user
     * @return Request to be sent to the server
     */
    public static Request setUserPermissionsReq(User user, Session session) {
        Request setPermsReq = new Request(ServerRequest.SET_USER_PERMISSION, session);
        setPermsReq.user = user;
        return setPermsReq;
    }

    /**
     * A method to generate a request for the server to change a user's password. N.B. Server needs to check whether the user actually exists
     * Possible to end up with a malformed Credentials Object
     *
     * @param proposedCredentials The proposed credentials of the new user
     * @param session             A Session object for an authenticated user
     * @return Request to be sent to the server
     */
    public static Request setPasswordReq(Credentials proposedCredentials, Session session) {
        Request setPassReq = new Request(ServerRequest.SET_USER_PASSWORD, session);
        setPassReq.credentials = proposedCredentials;
        return setPassReq;
    }

    /**
     * A method to generate a request for the server to delete a user, given their username
     *
     * @param username The username of the user to be deleted
     * @param session  A Session object for an authenticated user
     * @return Request to be sent to the server
     */
    public static Request deleteUserReq(String username, Session session) {
        Request delReq = new Request(ServerRequest.DELETE_USER, session);
        delReq.username = username;
        return delReq;
    }

    /**
     * A method to generate a request for the server to log out a user, AND INVALIDATE THE GIVEN SESSION
     *
     * @param session The session to be cancelled
     * @return Request to be sent to the server
     */
    public static Request logoutReq(Session session) {
        return new Request(ServerRequest.LOGOUT, session);
    }

    /**
     * Returns the billboard object associated with the request object.
     */
    public Billboard getBillboard() {
        return billboard;
    }

    /**
     * Returns the credentials object associated with the request object.
     */
    public Credentials getCredentials() {
        return credentials;
    }

    // FIXME - Permissions should be a separate class so we don't have to send a whole user object around with unnecessary content in it
    //

    /**
     * Returns the billboard name associated with the request object.
     */
    public String getBillboardName() {
        return billboardName;
    }

    /**
     * Returns the user object associated with the request object.
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the username associated with the request object.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the request's type associated with the request object.
     */
    public ServerRequest getRequestType() {
        return requestType;
    }

    /**
     * Returns the session object associated with the request object.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Returns a request with a session attached - the attachment should happen at the connector level
     *
     * @param session The session to attach
     * @return A new request with an attached session
     */
    public Request withSession(Session session) {
        this.session = session;
        return this;
    }

    /**
     * Sends the request to the server
     *
     * @param connector The connection to the server to send the request through
     * @return The response from the server
     * @throws IOException If the connection fails
     */
    public Response Send(ClientConnector connector) throws IOException {
        return connector.sendRequest(this);
    }


    public byte[] serialise() {
        // TODO - determine if this code is redundant.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }


    public Schedule getSchedule() {
        return schedule;
    }
}

