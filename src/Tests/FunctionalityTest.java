package Tests;

import BillboardSupport.Billboard;
import BillboardSupport.DummyBillboards;
import BillboardSupport.Schedule;
import Exceptions.*;
import Server.ClientHandler;
import Server.User;
import Server.blinkyDB;
import SocketCommunication.Credentials;
import SocketCommunication.Request;
import SocketCommunication.Response;
import SocketCommunication.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

/**
 * A suite of tests to ensure that permissions are being handled according to specification
 */
class FunctionalityTest {
    Session session;
    Session noperms_session;
    Function<Request, Response> respondTo;
    blinkyDB database;

    @BeforeEach
    void setUpAndMockData() throws IOException, SQLException, BillboardAlreadyExistsException {
        database = new blinkyDB(true, "testing");
        try {
            new User(new Credentials("Liran", "SeaMonkey123"), true, true, true, true, database);
        } catch (UserAlreadyExistsException e) {
            try {
                User user = new User("Liran", database);
                user.setCanCreateBillboards(true);
                user.setEditAllBillBoards(true);
                user.setEditUsers(true);
                user.setScheduleBillboards(true);
                database.UpdateUserDetails(user);
            } catch (NoSuchUserException ignored) {
            }
        }
        try {
            new User(new Credentials("Lira", "SeaMonkey123"), false, false, false, false, database);
        } catch (UserAlreadyExistsException e) {
            try {
                User nopermsuser = new User("Lira", database);
                nopermsuser.setCanCreateBillboards(false);
                nopermsuser.setEditAllBillBoards(false);
                nopermsuser.setEditUsers(false);
                nopermsuser.setScheduleBillboards(false);
                database.UpdateUserDetails(nopermsuser);
            } catch (NoSuchUserException ignored) {
            }
        }
        database.CreateViewer("localhost:5506");
        respondTo = new ClientHandler(null, null, null, database)::handleInboundRequest;
        Billboard billboard = DummyBillboards.messageAndInformationBillboard();
        billboard.setBillboardName("MyBill");
        database.createBillboard(billboard, "Lira");
        billboard.setBillboardName("MyOtherBill");
        database.createBillboard(billboard, "Liran");
        billboard.setBillboardName("YetAnotherBill");
        database.createBillboard(billboard, "Lira");
        setupAndLogin();
    }

    @Test
    void setupAndLogin() {
        // Create and send a login request
        noperms_session = (Session) respondTo.apply(Request.loginReq(
                new Credentials("Lira", "SeaMonkey123"))).getData();
        Response res = respondTo.apply(Request.loginReq(
                new Credentials("Liran", "SeaMonkey123")));
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
        if (!res.isStatus()) fail();
        // Verify logout by attempting to use the same session token to send an authenticated request
        assertFalse(respondTo.apply(Request.listAllBillboardsReq(session)).isStatus());
    }

    @Test
    void ViewerCurrentlyScheduled(){
        Request ScheduledBillboardRequest = Request.viewScheduledBillboardReq();

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
    void GetBillboards() {
        Request BillboardsRequest = Request.listAllBillboardsReq(session);

        Response res = respondTo.apply(BillboardsRequest);

        try {
            @SuppressWarnings({"unused", "unchecked"}) List<Billboard> billboards = (List<Billboard>) res.getData();
            assertTrue(res.isStatus());
        } catch (NullPointerException e) {
            fail();
        }
    }

    @Test
    void Delete_Billboard() {
        Response authedRes = respondTo.apply(Request.deleteBillboardReq("MyBill", session));
        Response unAuthedRes = respondTo.apply(Request.deleteBillboardReq("MyOtherBill", noperms_session));
        Response nonExistentBillboardRes = respondTo.apply(Request.deleteBillboardReq("Nonexistent", session));
        assertTrue(authedRes.isStatus() && !unAuthedRes.isStatus() && !nonExistentBillboardRes.isStatus());
    }

    @Test
    void Create_BillBoard() {
        Billboard billboard = DummyBillboards.messagePictureAndInformationBillboard();
        billboard.setBillboardName("654321");
        Response authedRes = respondTo.apply(Request.createBillboardReq(billboard, session));
        billboard.setBillboardName("54321");
        Response unAuthedRes = respondTo.apply(Request.createBillboardReq(billboard, noperms_session));
        Response noBillboardRes = respondTo.apply(Request.createBillboardReq(new Billboard(), session));
        assertTrue(authedRes.isStatus() && !unAuthedRes.isStatus() && !noBillboardRes.isStatus());
    }

    @Test
    void Edit_Billboard() {
        Billboard mock = DummyBillboards.messagePictureAndInformationBillboard();

        // User with edit billboards permission attempts to edit a billboard which exists
        Response authedRes = respondTo.apply(Request.editBillboardReq("MyOtherBill", mock, session));

        // User who does not have edit billboard permission attempts to edit a billboard which exists, but which they created

        Response unAuthedSameCreatorRes = respondTo.apply(Request.editBillboardReq("MyBill", mock, noperms_session));

        try {
            database.ScheduleBillboard("Mybill", new Schedule(Timestamp.valueOf(LocalDateTime.now()), 10, 30, "MyBill", Timestamp.valueOf(LocalDateTime.now())));
        } catch (SQLException e) {
            fail();
        }
        Response unAuthedSameCreatorScheduledRes = respondTo.apply(Request.editBillboardReq("MyBill", mock, noperms_session));
        Response unAuthedDifferentCreatorRes = respondTo.apply(Request.editBillboardReq("MyOtherBill", mock, noperms_session));
        Response nonExistentBillboardRes = respondTo.apply(Request.editBillboardReq("INVALID_BILLBOARD", mock, session));

        assertTrue(authedRes.isStatus() && unAuthedSameCreatorRes.isStatus() && !unAuthedSameCreatorScheduledRes.isStatus() && !nonExistentBillboardRes.isStatus() && !unAuthedDifferentCreatorRes.isStatus());
    }

    @Test
    void Get_Billboards() {
        Response GetExistentBillboard = respondTo.apply(Request.getBillboardInfoReq("MyBill", session));
        Response GetNonExistentBillboard = respondTo.apply(Request.getBillboardInfoReq("MyUnrealBill", session));

        assertTrue(GetExistentBillboard.isStatus() && !GetNonExistentBillboard.isStatus());
    }

    @Test
    void Schedule_Billboard() throws BillboardNotFoundException, SQLException {
        // Set a schedule for the billboard
        Billboard MyBill = database.getBillboard("MyBill");
        MyBill.setSchedule(new Schedule(Timestamp.valueOf(LocalDateTime.now()), 10, 30, "MyBill", Timestamp.valueOf(LocalDateTime.now())));
        Response scheduleRes = respondTo.apply(Request.scheduleBillboard(MyBill, session));
        Response scheduleResNoPerms = respondTo.apply(Request.scheduleBillboard(MyBill, noperms_session));

        assertTrue(scheduleRes.isStatus() && !scheduleResNoPerms.isStatus());
    }

    @Test
    void Remove_Scheduled() throws SQLException {
        database.ScheduleBillboard("MyBill", new Schedule(Timestamp.valueOf(LocalDateTime.now()), 10, 30, "MyBill", Timestamp.valueOf(LocalDateTime.now())));
        Response UnscheduleRes = respondTo.apply(Request.removeScheduledBillboardReq("MyBill", session));
        Response UnscheduleResNoPerms = respondTo.apply(Request.removeScheduledBillboardReq("MyBill", noperms_session));
        database.UnscheduleBillboard("Mybill");
        Response UnscheduleUnScheduledRes = respondTo.apply(Request.removeScheduledBillboardReq("MyBill", session));

        assertTrue(UnscheduleRes.isStatus() && !UnscheduleResNoPerms.isStatus() && !UnscheduleUnScheduledRes.isStatus());
    }

    @Test
    void userChangePassword() {
        Credentials userCredentials = noperms_session.serverUser.getSaltedCredentials();
        Credentials altUserCredentials = session.serverUser.getSaltedCredentials();

        // Try to change own password - should succeed
        Response changeSelf = respondTo.apply(Request.setPasswordReq(new Credentials(userCredentials.getUsername(), "Test"), noperms_session));

        // Try to change someone else's password - should fail
        Response changeOtherUser = respondTo.apply(Request.setPasswordReq(new Credentials(altUserCredentials.getUsername(), "Test"), noperms_session));

        // Non existent user
        Response changeNonExistentUser = respondTo.apply(Request.setPasswordReq(new Credentials("Non-existent user", "Test"), noperms_session));

        assertTrue(changeSelf.isStatus() && !changeOtherUser.isStatus() && !changeNonExistentUser.isStatus());
    }

    @Test
    void adminChangePassword() {
        Credentials userCredentials = noperms_session.serverUser.getSaltedCredentials();
        Credentials adminCredentials = session.serverUser.getSaltedCredentials();

        // Try to change own password - should succeed
        Response changeSelf = respondTo.apply(Request.setPasswordReq(new Credentials(adminCredentials.getUsername(), "Test"), session));

        // Try to change someone else's password - should succeed
        Response changeOtherUser = respondTo.apply(Request.setPasswordReq(new Credentials(userCredentials.getUsername(), "Test"), session));

        assertTrue(changeSelf.isStatus() && !changeOtherUser.isStatus());
    }

    @Test
    void deleteUser() {
        Credentials userCredentials = noperms_session.serverUser.getSaltedCredentials();
        Credentials adminCredentials = session.serverUser.getSaltedCredentials();

        // User attempts to exercise deletion power - should fail
        Response userAttemptDelete = respondTo.apply(Request.deleteUserReq(userCredentials.getUsername(), noperms_session));

        // Admin attempts to delete a user (who we know exists) - should succeed
        Response adminDeleteUser = respondTo.apply(Request.deleteUserReq(userCredentials.getUsername(), session));

        // Admin attempts to delete itself - should fail
        Response adminDeleteSelf = respondTo.apply(Request.deleteUserReq(adminCredentials.getUsername(), session));

        assertTrue(!userAttemptDelete.isStatus() && adminDeleteUser.isStatus() && !adminDeleteSelf.isStatus());
    }

    // Special case - admins cannot delete themselves
    @Test
    void deleteSelfAdmin() {
        Credentials adminCredentials = session.serverUser.getSaltedCredentials();

        Response adminDeleteSelf = respondTo.apply(Request.deleteUserReq(adminCredentials.getUsername(), session));

        assertFalse(adminDeleteSelf.isStatus());
    }

    @Test
    void changeUserPermissions() {
        User admin = session.serverUser;
        User user = noperms_session.serverUser;
        admin.setCanCreateBillboards(false);
        Response adminChangeOwn = respondTo.apply(Request.setUserPermissionsReq(admin, session));
        user.setCanCreateBillboards(true);
        Response adminChangeOther = respondTo.apply(Request.setUserPermissionsReq(user, session));
        user.setScheduleBillboards(true);
        Response userChangeOwn = respondTo.apply(Request.setUserPermissionsReq(admin, session));
        admin.setEditUsers(false);
        Response userChangeOther = respondTo.apply(Request.setUserPermissionsReq(admin, session));
        assertTrue(adminChangeOwn.isStatus() && adminChangeOther.isStatus() && !userChangeOwn.isStatus() && !userChangeOther.isStatus());
    }

    // A special case- the admin must not be able to remove edit users perm. from self ...
    @Test
    void adminRemoveOwnEditUserPermission() {
        User admin = session.serverUser;
        admin.setEditUsers(false);
        Response adminRemoveSelfPermissions = respondTo.apply(Request.setUserPermissionsReq(admin, session));

        assertFalse(adminRemoveSelfPermissions.isStatus());
    }
}
