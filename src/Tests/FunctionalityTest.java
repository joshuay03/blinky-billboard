package Tests;

import BillboardSupport.Billboard;
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
import Utils.Triple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.io.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static SocketCommunication.ServerRequest.*;

class FunctionalityTest {
    Session session;
    Session noperms_session;
    Function<Request, Response> respondTo;

    @BeforeAll
    static void setUsers() throws IOException, SQLException {
        blinkyDB db = new blinkyDB();
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
        Request LogOutRequest = new Request(LOGOUT, null, session);

        // Attempt to log out
        Response res = respondTo.apply(LogOutRequest);

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
        // Do not attempt to authenticate - pass null in as the session
        Request ScheduledBillboardRequest = new Request(VIEWER_CURRENTLY_SCHEDULED, null, null);

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
        Request BillboardsRequest = new Request(LIST_BILLBOARDS, null, session);

        Response res = respondTo.apply(BillboardsRequest);

        try{
            @SuppressWarnings({"unused", "unchecked"}) List<Billboard> billboards = (List<Billboard>) res.getData();
            assertTrue(res.isStatus());
        }
        catch (NullPointerException e){
            fail();
        }
    }

    @Test
    void Delete_Billboard(){
        Function<Number, Function<Session, Request>> EditBillboardRequest = (Number id) -> (Session session) -> new Request(DELETE_BILLBOARD, 0, session);
        Response authedRes = respondTo.apply(EditBillboardRequest.apply(0).apply(session));
        Response unAuthedRes = respondTo.apply(EditBillboardRequest.apply(0).apply(noperms_session));
        Response nonExistentBillboardRes = respondTo.apply(EditBillboardRequest.apply(999).apply(session));
        assertTrue(authedRes.isStatus() && !unAuthedRes.isStatus() && !nonExistentBillboardRes.isStatus());
    }

    @Test
    void Create_BillBoard(){
        Billboard mockBillboard = new Billboard(Color.WHITE, Color.BLACK, Color.DARK_GRAY, "Test Billboard", "This is a test billboard", null, null, 0, 0);
        Function<Session, Request> CreateBillboardRequest = (Session session) -> new Request(CREATE_BILLBOARD, mockBillboard, session);
        Response authedRes = respondTo.apply(CreateBillboardRequest.apply(session));
        Response unAuthedRes = respondTo.apply(CreateBillboardRequest.apply(noperms_session));
        Response noBillboardRes = respondTo.apply(new Request(CREATE_BILLBOARD, null, session));
        assertTrue(authedRes.isStatus() && !unAuthedRes.isStatus() && !noBillboardRes.isStatus());
    }

    @Test
    void Edit_Billboard(){
        Function<Number, Function<Session, Request>> EditBillboardRequest = (Number id) -> (Session session) -> new Request(EDIT_BILLBOARD, 0, session);
        Response authedRes = respondTo.apply(EditBillboardRequest.apply(0).apply(session));
        Response unAuthedSameCreatorRes = respondTo.apply(EditBillboardRequest.apply(0).apply(noperms_session));
        Response unAuthedDifferentCreatorRes = respondTo.apply(EditBillboardRequest.apply(1).apply(noperms_session));
        Response nonExistentBillboardRes = respondTo.apply(EditBillboardRequest.apply(999).apply(session));
        assertTrue(authedRes.isStatus() && unAuthedSameCreatorRes.isStatus() && !nonExistentBillboardRes.isStatus() && !unAuthedDifferentCreatorRes.isStatus());
    }

    @Test
    void Get_Billboards(){
        Function<String, Function<String, Request>> SearchBillboards = (String searchType) -> (String searchQuery) -> new Request(GET_BILLBOARD_INFO, new String[]{searchType, searchQuery}, session);
        Response creatorSearchRes = respondTo.apply(SearchBillboards.apply("billboard_id").apply("0"));
        Response durationSearchRes = respondTo.apply(SearchBillboards.apply("duration").apply("10"));
        Response emptySearchRes = respondTo.apply(SearchBillboards.apply("duration").apply("100000000"));

        assertTrue(creatorSearchRes.isStatus() && durationSearchRes.isStatus() && emptySearchRes.isStatus() && ((List<Billboard>) emptySearchRes.getData()).isEmpty());
    }

    @Test
    void Schedule_Billboard(){
        Function<Session, Request> ScheduleRequestCreator = (Session session) -> new Request(SCHEDULE_BILLBOARD, new Triple<Number, LocalDateTime, Number>(0, LocalDateTime.now(), 5), session);
        Response scheduleRes = respondTo.apply(ScheduleRequestCreator.apply(session));
        Response scheduleResNoPerms = respondTo.apply(ScheduleRequestCreator.apply(noperms_session));

        assertTrue(scheduleRes.isStatus() && !scheduleResNoPerms.isStatus());
    }

    @Test
    void Remove_Scheduled(){
        Function<Session, Request> ScheduleRequestCreator = (Session session) -> new Request(SCHEDULE_BILLBOARD, 0, session);
        Response scheduleRes = respondTo.apply(ScheduleRequestCreator.apply(session));
        Response scheduleResNoPerms = respondTo.apply(ScheduleRequestCreator.apply(noperms_session));

        assertTrue(scheduleRes.isStatus() && !scheduleResNoPerms.isStatus());
    }
}
