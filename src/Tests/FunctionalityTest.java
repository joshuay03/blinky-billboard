package Tests;

import BillboardSupport.Billboard;
import BillboardSupport.DummyBillboards;
import Exceptions.InvalidTokenException;
import Exceptions.NoSuchUserException;
import Exceptions.UserAlreadyExistsException;
import Server.ClientHandler;
import Server.Token;
import Server.User;
import Server.blinkyDB;
import SocketCommunication.Credentials;
import SocketCommunication.Request;
import SocketCommunication.Response;
import SocketCommunication.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

import static SocketCommunication.ServerRequest.*;

/**
 * A suite of tests to ensure that permissions are being handled according to specification
 */
class FunctionalityTest {
    Session session;
    Session noperms_session;
    Function<Request, Response> respondTo;

    @BeforeAll
    static void setUsersAndMockDBData() throws IOException, SQLException {
        blinkyDB db = new blinkyDB(true, "testing");
        try {
            new User(new Credentials("Liran", "SeaMonkey123"), true, true, true, true, db);
        } catch (UserAlreadyExistsException e) {
            try {
                User user = new User("Liran", db);
                user.setCanCreateBillboards(true, db);
                user.setEditAllBillBoards(true, db);
                user.setEditUsers(true, db);
                user.setScheduleBillboards(true, db);
            } catch (NoSuchUserException ignored) {}
        }
        try {
            new User(new Credentials("Lira", "SeaMonkey123"), false, false, false, false, db);
        } catch (UserAlreadyExistsException e) {
            try {
                User nopermsuser = new User("Lira", db);
                nopermsuser.setCanCreateBillboards(false, db);
                nopermsuser.setEditAllBillBoards(false, db);
                nopermsuser.setEditUsers(false, db);
                nopermsuser.setScheduleBillboards(false, db);
            } catch (NoSuchUserException ignored) {}
        }
        // Write other mock data
        db.createBillboard(DummyBillboards.messageAndInformationBillboard(), "Lira");
        db.createBillboard(DummyBillboards.pictureAndInformationBillboard(), "Liran");
        db.createBillboard(DummyBillboards.pictureOnlyBillboard(), "Lira");
        db.CreateViewer("localhost:5506");
    }

    @BeforeEach @Test
    void setUpAndLogin() throws SQLException, IOException{
        Function<Credentials, Request> MakeLoginRequest = (Credentials credentials) -> new Request(LOGIN, credentials, null);
        respondTo = new ClientHandler(null, null, null, new blinkyDB())::handleInboundRequest;
        // Create and send a login request
        noperms_session = (Session) respondTo.apply(MakeLoginRequest.apply(
                new Credentials("Lira", "SeaMonkey123"))).getData();
        Credentials credentials = new Credentials("Liran", "SeaMonkey123");
        Response res = respondTo.apply(MakeLoginRequest.apply(credentials));
        if (res.isStatus()){
            // Set the session token
            session = (Session) res.getData();
            assertTrue(res.isStatus());
        }
        else fail();
    }

    @Test
    void SendLogOut(){

        // Attempt to log out
        Response res = respondTo.apply(Request.logoutReq(session));

        try {
            // Expire the session
            session = (Session) res.getData();
            // Verify logout
            Token.validate(session.token);
            // If the token validates
            fail();
        }
        catch (InvalidTokenException | NullPointerException | ClassCastException e){
            // If the response was unsucessful/token validation failed after logout
            assertTrue(res.isStatus());
        }
    }

    @Test
    void ViewerCurrentlyScheduled(){
        Request ScheduledBillboardRequest = Request.scheduledBillboardReq();

        // Retrieve billboard from request
        Response res = respondTo.apply(ScheduledBillboardRequest);
        try{
            @SuppressWarnings("unused") Billboard ScheduledBillboard = (Billboard) res.getData(); // Statement is necessary to verify that a valid billboard was received
            assertTrue(res.isStatus());
        }
        catch (NullPointerException e){
            // If response wasn't a valid billboard
            fail();
        }
    }

    @Test
    void GetBillboards(){
        Request BillboardsRequest = Request.listAllBillboardsReq(session);

        Response res = respondTo.apply(BillboardsRequest);

        try{
            @SuppressWarnings({"unused", "unchecked"}) List<Billboard> billboards = (List<Billboard>) res.getData();
            assertTrue(res.isStatus());
        }
        catch (NullPointerException e){
            fail();
        }
    }

    static final int    VALID_BILLBOARD = 0,
                        VALID_BILLBOARD_CREATED_BY_UNAUTHORISED_USER = 1,
                        CURRENTLY_SCHEDULED_BILLBOARD = 2,
                        INVALID_BILLBOARD = 999;
    @Test
    void Delete_Billboard(){

        Response authedRes = respondTo.apply(Request.deleteBillboardReq(VALID_BILLBOARD, session));
        Response unAuthedRes = respondTo.apply(Request.deleteBillboardReq(VALID_BILLBOARD, noperms_session));
        Response nonExistentBillboardRes = respondTo.apply(EditBillboardRequest.apply(INVALID_BILLBOARD).apply(session));
        assertTrue(authedRes.isStatus() && !unAuthedRes.isStatus() && !nonExistentBillboardRes.isStatus());
    }

    @Test
    void Create_BillBoard(){

        Response authedRes = respondTo.apply(Request.createBillboardReq(DummyBillboards.messagePictureAndInformationBillboard(), session));
        Response unAuthedRes = respondTo.apply(Request.createBillboardReq(DummyBillboards.messagePictureAndInformationBillboard(), noperms_session);
        Response noBillboardRes = respondTo.apply(null, session);
        assertTrue(authedRes.isStatus() && !unAuthedRes.isStatus() && !noBillboardRes.isStatus());
    }

    @Test
    void Edit_Billboard(){
        Billboard mock = DummyBillboards.messagePictureAndInformationBillboard();

        // User with edit billboards permission attempts to edit a billboard which exists
        Response authedRes = respondTo.apply(Request.editBillboardReq(VALID_BILLBOARD, mock, session));

        // User who does not have edit billboard permission attempts to edit a billboard which exists, but which they created
        // FIXME - I don't think this test does what it purports to do - if it does, consider reframing the tests so that the context of what is going on is mroe obvious

        Response unAuthedSameCreatorRes = respondTo.apply(Request.editBillboardReq(VALID_BILLBOARD_CREATED_BY_UNAUTHORISED_USER, mock, noperms_session));
        Response unAuthedSameCreatorScheduledRes = respondTo.apply(Request.editBillboardReq(CURRENTLY_SCHEDULED_BILLBOARD, mock, noperms_session));

        // FIXME - I think this one again does something different to what it purports to do
        Response unAuthedDifferentCreatorRes = respondTo.apply(Request.editBillboardReq(VALID_BILLBOARD_CREATED_BY_UNAUTHORISED_USER, mock, noperms_session));
        Response nonExistentBillboardRes = respondTo.apply(Request.editBillboardReq(INVALID_BILLBOARD, mock, session));
        assertTrue(authedRes.isStatus() && unAuthedSameCreatorRes.isStatus() && !unAuthedSameCreatorScheduledRes.isStatus() && !nonExistentBillboardRes.isStatus() && !unAuthedDifferentCreatorRes.isStatus());
    }

    //FIXME - underlying function needs to be amended
    /* DEPRECATED TEST - unnecessary for spec compliance
    @Test
    void Get_Billboards(){
        Function<String, Function<String, Request>> SearchBillboards = (String searchType) -> (String searchQuery) -> new Request(GET_BILLBOARD_INFO, new String[]{searchType, searchQuery}, session);
        Response creatorSearchRes = respondTo.apply(Request.getBillboardInfoReq())
        Response creatorSearchRes = respondTo.apply(SearchBillboards.apply("billboard_id").apply("0"));
        Response durationSearchRes = respondTo.apply(SearchBillboards.apply("duration").apply("10"));
        Response emptySearchRes = respondTo.apply(SearchBillboards.apply("duration").apply("100000000"));

        assertTrue(creatorSearchRes.isStatus() && durationSearchRes.isStatus() && emptySearchRes.isStatus() && ((List<Billboard>) emptySearchRes.getData()).isEmpty());
    }*/

    //TODO implement spec-compliant test
    @Test
    void Get_Billboards(){

    }

    @Test
    void Schedule_Billboard(){
        Billboard mock = DummyBillboards.messageAndPictureBillboard();
        // Set a schedule for the billboard

        Response scheduleRes = respondTo.apply(Request.scheduleBillboard(mock, session));
        Response scheduleResNoPerms = respondTo.apply(Request.scheduleBillboard(mock, noperms_session));

        assertTrue(scheduleRes.isStatus() && !scheduleResNoPerms.isStatus());
    }

    @Test
    void Remove_Scheduled(){

        Response scheduleRes = respondTo.apply(Request.removeScheduledBillboardReq(VALID_BILLBOARD));
        Response scheduleResNoPerms = respondTo.apply(Request.removeScheduledBillboardReq(VALID_BILLBOARD, noperms_session));

        assertTrue(scheduleRes.isStatus() && !scheduleResNoPerms.isStatus());
    }

    @Test
    void userChangePassword(){
        Credentials userCredentials = noperms_session.serverUser.getSaltedCredentials();
        Credentials altUserCredentials = session.serverUser.getSaltedCredentials();

        // Try to change own password - should succeed
        Response changeSelf = respondTo.apply(Request.setPasswordReq(new Credentials(userCredentials.getUsername(), "Test")), noperms_session);

        // Try to change someone else's password - should fail
        Response changeOtherUser = respondTo.apply(Request.setPasswordReq(new Credentials(altUserCredentials, "Test")), noperms_session);

        // Non existent user
        Response changeNonExistentUser = respondTo.apply(Request.setPasswordReq(new Credentials("Non-existent user", "Test")), noperms_session);

        assertTrue(changeSelf.isStatus() && !changeOtherUser.isStatus() && !changeNonExistentUser.isStatus());
    }

    @Test
    void adminChangePassword(){
        Credentials userCredentials = noperms_session.serverUser.getSaltedCredentials();
        Credentials adminCredentials = session.serverUser.getSaltedCredentials();

        // Try to change own password - should succeed
        Response changeSelf = respondTo.apply(Request.setPasswordReq(new Credentials(adminCredentials.getUsername(), "Test"), session));

        // Try to change someone else's password - should succeed
        Response changeOtherUser = respondTo.apply(Request.setPasswordReq(new Credentials(adminCredentials, "Test")), session);

        assertTrue(changeSelf.isStatus() && !changeOtherUser.isStatus());
    }

    @Test
    void deleteUser(){
        Credentials userCredentials = noperms_session.serverUser.getSaltedCredentials();
        Credentials adminCredentials = session.serverUser.getSaltedCredentials();

        // User attempts to exercise deletion power - should fail
        Response userAttemptDelete = respondTo.apply(Request.deleteUserReq(userCredentials.getUsername(), noperms_session));

        // Admin attempts to delete a user (who we know exists) - should succeed
        Response adminDeleteUser = respondTo.apply(Request.deleteUserReq(userCredentials.getUsername(), session));

        assertTrue(!userAttemptDelete.isStatus() && adminDeleteUser.isStatus() && !adminDeleteSelf.isStatus());
    }

    // Special case - admins cannot delete themselves
    @Test
    void deleteUser() {
        Credentials adminCredentials = session.serverUser.getSaltedCredentials();


        Response adminDeleteSelf = respondTo.apply(Request.deleteUserReq(adminCredentials.getUsername(), session));

        assertTrue(!adminDeleteSelf.isStatus());
    }
    @Test
    void changeUserPermissions(){

        assertTrue(!adminDeleteSelf.isStatus());
    }

    // A special case- the admin must not be able to remove edit users perm. from self ...
    @Test
    void adminRemoveOwnEditUserPermission(){
        Credentials adminCredentials = session.serverUser.getSaltedCredentials();

        //... should fail
        Response adminRemoveSelfPermissions = respondTo.apply(Request.setUserPermissionsReq(session.serverUser), session));

        assertTrue(!adminDeleteSelf.isStatus());
    }

    @AfterAll()
    static void Reset_Schema() throws IOException, SQLException {
        new blinkyDB(true);
    }
}
