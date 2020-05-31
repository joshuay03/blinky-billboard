package Server;

import BillboardSupport.Billboard;
import BillboardSupport.DummyBillboards;
import BillboardSupport.Schedule;
import Exceptions.BillboardAlreadyExistsException;
import Exceptions.BillboardNotFoundException;
import Exceptions.BillboardUnscheduledException;
import Exceptions.InvalidTokenException;
import SocketCommunication.Credentials;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is used for instantiating a new database object and
 * specifies the various queries which can be made to the database.
 * Used heavily by the ClientHandler class.
 * @see ClientHandler
 */
public class blinkyDB {
    final private DBProps props;
    final private Connection dbconn;

    //TODO insert constants which correspond to database columns
    public enum Columns {

    }

    /**
     * Database object constructor
     *
     * @throws IOException  If db.props isn't found
     * @throws SQLException If there's a problem connecting to the database
     */
    public blinkyDB(Boolean dropSchema, String overrideSchemaName) throws IOException, SQLException { // Create a new database object - attempting to populate an actual database if one isn't already initialised. Then, start a connection to the database.
        if (overrideSchemaName != null) props = new DBProps(overrideSchemaName); // Read db.props
        else props = new DBProps();
        // Ensure the schema exists
        {
            Connection init_schema = DriverManager.getConnection("jdbc:mariadb://" + props.url, props.username, props.password);
            if (dropSchema) init_schema.createStatement().executeQuery("DROP DATABASE IF EXISTS " + props.schema);
            init_schema.createStatement().executeQuery("CREATE DATABASE IF NOT EXISTS " + props.schema);
            init_schema.close();
        }
        // Start a database connection
        dbconn = DriverManager.getConnection("jdbc:mariadb://" + props.url + "/" + props.schema, props.username, props.password);
        // Try to initialise based on sql file
        Path sqlInitFile = Paths.get(new File("blinkybillboard.sql").getPath());
        String[] batch = new String(Files.readAllBytes(sqlInitFile)).split("(?<=;)");
        dbconn.createStatement().executeQuery(String.format("USE %s;", props.schema));
        // Execute all statements in the array
        for (String toExec : batch) {
            if (!toExec.trim().isEmpty()) // (don't execute empty statements)
                dbconn.createStatement().executeQuery(toExec);
        }
        try {
            dbconn.createStatement().executeQuery("INSERT INTO `Viewers` (viewer_id, socket) VALUES (1, \"localhost:5508\");");
        } catch (SQLException ignored){} // Only try to insert if it's not already there
    }

    /**
     * Instantiates a new blinkyDB object
     * @throws IOException  If db.props isn't found
     * @throws SQLException If there's a problem connecting to the database
     */
    public blinkyDB() throws IOException, SQLException {
        blinkyDB newDB = new blinkyDB(false, null);
        this.dbconn = newDB.dbconn;
        this.props = newDB.props;
    }

    /**
     * Method used for obtaining a list of all currently listed billboards
     * @param searchQuery mat billboards against this query
     * @param searchType type off billboard
     * @return
     * @throws SQLException
     */
    public List<Billboard> getBillboards(String searchQuery, String searchType) throws SQLException {
        PreparedStatement getBillboards;
        final String billboardLookUpString = (searchQuery != null && searchType != null) ?
                "select * from Billboards where ? like \"%?%\"" : "select * from Billboards";
        dbconn.setAutoCommit(false);
        getBillboards = dbconn.prepareStatement(billboardLookUpString);
        if (billboardLookUpString.contains("?")) {
            getBillboards.setString(1, searchType);
            getBillboards.setString(2, searchQuery);
        }
        dbconn.setAutoCommit(true);
        ResultSet rs = getBillboards.executeQuery();
        List<Billboard> BillboardList = new ArrayList<>();

        while (rs.next()) {
            // For each returned billboard from the database
            Object image;
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(rs.getBytes("billboardImage"));
                ObjectInput in = new ObjectInputStream(bis);
                image = in.readObject();
            } catch (Exception e) {
                image = null;
            }
            Billboard current = new Billboard();
            current.setBillboardName(rs.getString("billboard_name"));
            current.setCreator(rs.getString("creator"));
            current.setBackgroundColour(new Color(rs.getInt("backgroundColour")));
            current.setMessageColour(new Color(rs.getInt("messageColour")));
            current.setInformationColour(new Color(rs.getInt("informationColour")));
            current.setMessage(rs.getString("message"));
            current.setInformation(rs.getString("information"));
            current.setImageData((String) image);
            BillboardList.add(current);
        }
        return BillboardList;
    }

    /***
     * List all billboards.
     * Calls first getBillboards() method with null values inserted.
     * @return
     * @throws SQLException
     */
    public List<Billboard> getBillboards() throws SQLException {
        return this.getBillboards(null, null);
    }

    /**
     * Gets a specific billboard from the database.
     * @param name The name of the billboard.
     * @return
     * @throws BillboardNotFoundException
     * @throws SQLException
     */
    public Billboard getBillboard(String name) throws BillboardNotFoundException, SQLException {
        PreparedStatement getBillboard;
        final String billboardLookUpString = "select * from Billboards where billboard_name = ?";
        dbconn.setAutoCommit(false);
        getBillboard = dbconn.prepareStatement(billboardLookUpString);
        getBillboard.setString(1, name);
        dbconn.setAutoCommit(true);
        ResultSet rs = getBillboard.executeQuery();
        try {
            boolean found = rs.first();
            if (!found) throw new BillboardNotFoundException(name); // If there is no result, throw an exception
        } // Go to the result
        catch (SQLException e) {throw new BillboardNotFoundException(name);} // If there is no result, throw an exception
        // Process billboard data
        Object image;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(rs.getBytes("billboardImage"));
           ObjectInput in = new ObjectInputStream(bis);
            image = in.readObject();
        } catch (Exception e) {
            image = null;
        }
        try {
            Billboard billboard = new Billboard();
            billboard.setBillboardName(rs.getString("billboard_name"));
            billboard.setCreator(rs.getString("creator"));
            billboard.setBackgroundColour(new Color(rs.getInt("backgroundColour")));
            billboard.setMessageColour(new Color(rs.getInt("messageColour")));
            billboard.setInformationColour(new Color(rs.getInt("informationColour")));
            billboard.setMessage(rs.getString("message"));
            billboard.setInformation(rs.getString("information"));
            billboard.setImageData((String) image);
            return billboard;
        } catch (SQLDataException e) {
            return null;
        }
    }

    /**
     * Creates a new viewer object within the database
     * @param socket string value of the socket which the viewer is operating on.
     * @throws SQLException
     */
    public void CreateViewer(String socket) throws SQLException {
        String ViewerCreationString = "INSERT INTO blinkyBillboard.Viewers\n" +
                "(socket)\n" +
                "VALUES(?);\n";
        PreparedStatement ViewerInserter = dbconn.prepareStatement(ViewerCreationString);
        dbconn.setAutoCommit(false);
        ViewerInserter.setString(1, socket);
        ViewerInserter.executeUpdate();
        try {
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback();
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Method used to edit or override a billboard from within the database.
     * @param name The name of the billboard
     * @param backgroundColour The background color of the billboard
     * @param messageColour The message color of the billboard
     * @param informationColour The information color of the billboard
     * @param message The message associated with the billboard
     * @param information The information associated with the billboard
     * @param imageData The image data
     * @throws SQLException
     * @throws BillboardNotFoundException
     */
    @SuppressWarnings("ConstantConditions")
    public void editBillboard(String name, Color backgroundColour, Color messageColour, Color informationColour, String message, String information, String imageData) throws SQLException, BillboardNotFoundException {
        // Takes billboard properties, and applies them to the given id
        getBillboard(name); // Will throw an exception if the billboard doesn't exist
        byte[] SerialisedImage;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (imageData != null){
            try {
                new ObjectOutputStream(bos).writeObject(imageData);
                SerialisedImage = bos.toByteArray();
            } catch (IOException e) { SerialisedImage = null; }
        }
        else SerialisedImage = null;
        List<String> updateList = new ArrayList<>();
        if (backgroundColour != null) updateList.add("backgroundColour=?");
        if (messageColour != null) updateList.add("messageColour=?");
        if (informationColour != null) updateList.add("informationColour=?");
        if (message != null) updateList.add("message=?");
        if (information != null) updateList.add("information=?");
        if (imageData != null) updateList.add("billboardImage=?");
        String AttrsUpdateString = String.join(", ", updateList);
        if (AttrsUpdateString.isEmpty()) return;
        String BillboardInsertQuery = "UPDATE Billboards\n" +
                "SET " + AttrsUpdateString + "\n" +
                "WHERE billboard_name=?;\n";
        dbconn.setAutoCommit(false);
        PreparedStatement updateBillboard = dbconn.prepareStatement(BillboardInsertQuery);
        try {
            {int index = updateList.indexOf("backgroundColour=?"); if (index != -1) updateBillboard.setInt(index+1, backgroundColour.getRGB());}
            {int index = updateList.indexOf("messageColour=?"); if (index != -1) updateBillboard.setInt(index+1, messageColour.getRGB());}
            {int index = updateList.indexOf("informationColour=?"); if (index != -1) updateBillboard.setInt(index+1, informationColour.getRGB());}
            {int index = updateList.indexOf("message=?"); if (index != -1) updateBillboard.setString(index+1, message);}
            {int index = updateList.indexOf("information=?"); if (index != -1) updateBillboard.setString(index+1, information);}
            {int index = updateList.indexOf("billboardImage=?"); if (index != -1) updateBillboard.setBytes(index+1, SerialisedImage);}
            updateBillboard.setString(updateList.size()+1 ,name);
            updateBillboard.executeUpdate();
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback();
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Allows writing a billboard into the database
     *
     * @param billboard_in The billboard to write to the database
     * @param creator      The username of the billboard's creator
     * @throws SQLException If the creation fails
     */
    public void createBillboard(Billboard billboard_in, String creator) throws SQLException, BillboardAlreadyExistsException {
        assert creator != null;
        assert billboard_in.getBillboardName() != null;
        try {
            Billboard existingBillboard = getBillboard(billboard_in.getBillboardName());
            throw new BillboardAlreadyExistsException(existingBillboard);
        } catch (BillboardNotFoundException ex) {
            List<String> creationList = new ArrayList<>();
            if (billboard_in.getBackgroundColour() != null) creationList.add(", backgroundColour");
            if (billboard_in.getMessageColour() != null) creationList.add(", messageColour");
            if (billboard_in.getInformationColour() != null) creationList.add(", informationColour");
            if (billboard_in.getMessage() != null) creationList.add(", message");
            if (billboard_in.getInformation() != null) creationList.add(", information");
            if (billboard_in.getImageData() != null) creationList.add(", billboardImage");
            String AttrsToInsertString = String.join("", creationList);
            String BillboardInsertQuery = "INSERT INTO Billboards\n" +
                    "(billboard_name, creator" + AttrsToInsertString + ")\n" +
                    "VALUES(?, ?" + creationList.stream().map(s -> ", ?").collect(Collectors.joining()) + ");\n";
            dbconn.setAutoCommit(false);
            byte[] SerialisedImage;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                new ObjectOutputStream(bos).writeObject(billboard_in.getImageData());
                SerialisedImage = bos.toByteArray();
            } catch (IOException e) {
                SerialisedImage = null;
            }
            PreparedStatement insertBillboard = dbconn.prepareStatement(BillboardInsertQuery);
            try {
                insertBillboard.setString(1, billboard_in.getBillboardName());
                insertBillboard.setString(2, creator);
                {int index = creationList.indexOf(", backgroundColour"); if (index != -1) insertBillboard.setInt(index+3, billboard_in.getBackgroundColour().getRGB());}
                {int index = creationList.indexOf(", messageColour"); if (index != -1) insertBillboard.setInt(index+3, billboard_in.getMessageColour().getRGB());}
                {int index = creationList.indexOf(", informationColour"); if (index != -1) insertBillboard.setInt(index+3, billboard_in.getInformationColour().getRGB());}
                {int index = creationList.indexOf(", message"); if (index != -1) insertBillboard.setString(index+3, billboard_in.getMessage());}
                {int index = creationList.indexOf(", information"); if (index != -1) insertBillboard.setString(index+3, billboard_in.getInformation());}
                {int index = creationList.indexOf(", billboardImage"); if (index != -1) insertBillboard.setBytes(index+3, SerialisedImage);}
                insertBillboard.executeUpdate();
                dbconn.commit();
            } catch (SQLException e) {
                dbconn.rollback();
                throw e;
            }
            dbconn.setAutoCommit(true);
        }
    }

    /**
     * A method for the User constructor to read user data from the database
     *
     * @param username Username
     * @return The details of said user
     * @throws SQLException If the user lookup fails
     */
    protected ResultSet LookUpUserDetails(String username) throws SQLException {
        PreparedStatement UserLookUp; // Create the prepared statement object
        String userLookUpString = "select * from Users where user_name = ?"; // Define the query to run
        dbconn.setAutoCommit(false);

        UserLookUp = dbconn.prepareStatement(userLookUpString); // Compile the statement

        UserLookUp.setString(1, username); // Insert the provided username into the query

        dbconn.setAutoCommit(true);
        return UserLookUp.executeQuery(); // Run the query
    }

    /**
     * Retrieves the details of all users contained within the database.
     * @return A result set of all user details.
     * @throws SQLException
     */
    protected ResultSet LookUpAllUserDetails() throws SQLException {
        PreparedStatement UserLookUp; // Create the prepared statement object
        String userLookUpString = "select * from Users"; // Define the query to run
        dbconn.setAutoCommit(false);

        UserLookUp = dbconn.prepareStatement(userLookUpString); // Compile the statement

        dbconn.setAutoCommit(true);
        return UserLookUp.executeQuery(); // Run the query
    }

    /**
     * Get all schedules from the specified start time onwards
     *
     * @param time The earliest start time to get schedules of
     * @return The schedules
     * @throws SQLException If the lookup fails
     */
    public List<Schedule> getSchedules(Timestamp time) throws SQLException {
        String scheduleLookup = "SELECT * FROM Scheduling WHERE start_time < ?";
        PreparedStatement ScheduleLookUp;
        dbconn.setAutoCommit(false);
        ScheduleLookUp = dbconn.prepareStatement(scheduleLookup);
        ScheduleLookUp.setTimestamp(1, time);
        dbconn.setAutoCommit(true);
        ResultSet rs = ScheduleLookUp.executeQuery();
        List<Schedule> ScheduleList = new ArrayList<>();
        while (rs.next()) {
            try {
                Timestamp startTime = rs.getTimestamp("start_time");
                int repeatInterval = rs.getInt("interval");
                int duration = rs.getInt("duration");
                String billboardName = rs.getString("billboard_name");
                Timestamp scheduledAt = rs.getTimestamp("scheduled_at");
                ScheduleList.add(new Schedule(startTime, duration, repeatInterval, billboardName, scheduledAt));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ScheduleList;
    }

    /**
     * Put in billboard Name - get schedule (including future schedules)
     *
     * @param name The billboard name
     * @return The schedule of that billboard
     * @throws SQLException if the lookup fails
     */
    public Schedule getScheduleForBillboard(String name) throws SQLException {
        String scheduleLookup = "SELECT * FROM Scheduling WHERE billboard_name = ?";
        PreparedStatement scheduleLookUpForBillboard;
        dbconn.setAutoCommit(false);
        scheduleLookUpForBillboard = dbconn.prepareStatement(scheduleLookup);
        scheduleLookUpForBillboard.setString(1, name);
        dbconn.setAutoCommit(true);
        ResultSet rs = scheduleLookUpForBillboard.executeQuery();
        List<Schedule> ScheduleList = new ArrayList<>();
        while (rs.next()) {
            try {
                Timestamp startTime = rs.getTimestamp("start_time");
                int repeatInterval = rs.getInt("interval");
                int duration = rs.getInt("duration");
                String billboardName = rs.getString("billboard_name");
                Timestamp scheduledAt = rs.getTimestamp("scheduled_at");
                ScheduleList.add(new Schedule(startTime, duration, repeatInterval, billboardName, scheduledAt));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ScheduleList.get(0);
    }

    /**
     * Takes a billboard, and assigns a given schedule to it
     *
     * @param billboard_name The billboard's name
     * @param schedule  The schedule to assign to the billboard
     */
    public void ScheduleBillboard(String billboard_name, Schedule schedule) throws SQLException {
        String SchedulingString = "INSERT INTO Scheduling\n" +
                "(billboard_name, viewer_id, start_time, duration, `interval`)\n" +
                "VALUES(?, ?, ?, ?, ?);\n";

        PreparedStatement CreateSchedule;
        dbconn.setAutoCommit(false);
        try {
            CreateSchedule = dbconn.prepareStatement(SchedulingString);

            CreateSchedule.setString(1, billboard_name);
            CreateSchedule.setInt(2, 1);
            CreateSchedule.setTimestamp(3, schedule.StartTime);
            CreateSchedule.setInt(4, schedule.duration);
            CreateSchedule.setInt(5, schedule.repeatInterval);

            CreateSchedule.executeUpdate();
            dbconn.commit();

        } catch (SQLException e) {
            dbconn.rollback();
        }

        dbconn.setAutoCommit(true);
    }

    /**
     * This method takes new user details and adds them to the database.
     *
     * @param credentials         The new user's credentials
     * @param CanCreateBillboards permission
     * @param EditAllBillBoards   permission
     * @param ScheduleBillboards  permission
     * @param EditUsers           permission
     * @throws SQLException // If the insertion fails, such as in the case when such a user already exists in the database.
     */
    protected void RegisterUserInDatabase(Credentials credentials, boolean CanCreateBillboards, boolean EditAllBillBoards, boolean ScheduleBillboards, boolean EditUsers) throws SQLException {
        char[] permissions = new char[4];
        if (CanCreateBillboards) permissions[0] = 'B';
        if (EditAllBillBoards) permissions[1] = 'E';
        if (ScheduleBillboards) permissions[2] = 'S';
        if (EditUsers) permissions[3] = 'U';

        // Generate a random salt
        byte[] salt = new byte[100];
        new Random().nextBytes(salt);

        String userInsertionString = "INSERT INTO Users\n(user_name, user_permissions, password_hash, salt)\nVALUES(?, ?, ?, ?);";
        byte[] Salted = AuthenticationHandler.HashPasswordHashSalt(credentials.getPasswordHash(), salt);

        dbconn.setAutoCommit(false);

        PreparedStatement UserInserter = dbconn.prepareStatement(userInsertionString); // Prepare the insertion statement
        try {
            UserInserter.setString(1, credentials.getUsername());
            UserInserter.setString(2, new String(permissions));
            UserInserter.setBytes(3, Salted);
            UserInserter.setBytes(4, salt);

            UserInserter.executeUpdate();
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback(); // Try to rollback
            throw e;
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Updates user details
     *
     * @param user User object to update the details of
     * @throws SQLException when the update fails
     */
    public void UpdateUserDetails(User user) throws SQLException {
        char[] permissions = new char[4];
        if (user.CanCreateBillboards()) permissions[0] = 'B';
        if (user.CanEditAllBillboards()) permissions[1] = 'E';
        if (user.CanScheduleBillboards()) permissions[2] = 'S';
        if (user.CanEditUsers()) permissions[3] = 'U';

        String userUpdateString = "UPDATE Users\n" +
                "SET user_permissions=?, password_hash=?, salt=?\n" +
                "WHERE user_name=?;";

        dbconn.setAutoCommit(false);
        PreparedStatement updateUser = dbconn.prepareStatement(userUpdateString);
        try {
            updateUser.setString(1, new String(permissions));
            updateUser.setBytes(2, user.getSaltedCredentials().getPasswordHash());
            updateUser.setBytes(3, user.salt);
            updateUser.setString(4, user.getSaltedCredentials().getUsername());

            updateUser.executeUpdate();
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback();
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Deletes users from the database
     *
     * @param username The username to delete
     * @throws SQLException If the deletion fails
     */
    public void DeleteUser(String username) throws SQLException {
        String UserDeletionString = "DELETE FROM Users\n" +
                "WHERE user_name=?;\n";
        String BillboardDisassociationString = "UPDATE Billboards\n" +
                "SET creator=NULL\n" +
                "WHERE creator = ?;";

        PreparedStatement UserDeleter = dbconn.prepareStatement(UserDeletionString);
        PreparedStatement BillboardDisassociator = dbconn.prepareStatement(BillboardDisassociationString);

        dbconn.setAutoCommit(false);
        try {
            UserDeleter.setString(1, username);
            BillboardDisassociator.setString(1, username);

            BillboardDisassociator.executeUpdate();
            UserDeleter.executeUpdate();
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback();
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Register a viewer in the database
     *
     * @param Socket The viewer's socket (IP + port)
     * @throws SQLException If the registration fails
     */
    protected void RegisterViewer(String Socket) throws SQLException {
        String ViewerInserterString = "INSERT INTO Viewers (socket) VALUES(?);";
        PreparedStatement ViewerInserter = dbconn.prepareStatement(ViewerInserterString);

        dbconn.setAutoCommit(false);
        try {
            ViewerInserter.setString(1, Socket);

            ViewerInserter.executeUpdate();
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback();
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Remove a viewer from the database
     *
     * @param id The id of the viewer to delete
     * @throws SQLException If the deletion fails
     */
    protected void UnRegisterViewer(int id) throws SQLException {
        String ViewerDeleterString = "DELETE FROM Viewers\n" +
                "WHERE viewer_id=?;\n";
        PreparedStatement ViewerDeleter = dbconn.prepareStatement(ViewerDeleterString);

        dbconn.setAutoCommit(false);
        try {
            ViewerDeleter.setInt(1, id);
            ViewerDeleter.executeUpdate();
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback();
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Deletes schedules for a given billboard ID
     *
     * @param name Billboard Name
     * @throws SQLException If the deletion fails
     */
    public void UnscheduleBillboard(String name) throws SQLException, BillboardNotFoundException, BillboardUnscheduledException {
        String SchedulesDeletionString = "DELETE FROM Scheduling\n" +
                "WHERE billboard_name=?;\n";

        PreparedStatement SchedulesDeleter = dbconn.prepareStatement(SchedulesDeletionString);
        dbconn.setAutoCommit(false);
        try {
            SchedulesDeleter.setString(1, name);
            int unscheduled = SchedulesDeleter.executeUpdate();
            if (unscheduled == 0){
                dbconn.rollback();
                Billboard billboard;
                try {
                    billboard = this.getBillboard(name);
                } catch (BillboardNotFoundException e) {
                    throw new BillboardNotFoundException(name);
                }
                throw new BillboardUnscheduledException(billboard);
            }
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback();
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Deletes a billboard and all schedules associated with it
     *
     * @param name The billboard name to delete
     * @throws SQLException If the deletion fails
     */
    public void DeleteBillboard(String name) throws SQLException, BillboardNotFoundException {
        String BillboardDeletionString = "DELETE FROM Billboards\n" +
                "WHERE billboard_name=?;\n";
        String SchedulesDeletionString = "DELETE FROM Scheduling\n" +
                "WHERE billboard_name=?;\n";
        PreparedStatement BillboardDeleter = dbconn.prepareStatement(BillboardDeletionString);
        PreparedStatement SchedulesDeleter = dbconn.prepareStatement(SchedulesDeletionString);
        dbconn.setAutoCommit(false);
        try {
            SchedulesDeleter.setString(1, name);
            BillboardDeleter.setString(1, name);
            SchedulesDeleter.executeUpdate();
            boolean deleted = BillboardDeleter.executeUpdate() > 0;
            if (!deleted) {
                dbconn.rollback();
                throw new BillboardNotFoundException(name);
            }
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback();
            throw e;
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Get all viewers with their sockets
     *
     * @return The viewers
     * @throws SQLException If the lookup fails
     */
    protected ResultSet GetViewers() throws SQLException {
        String ViewersSelectorString = "SELECT viewer_id, socket\n" +
                "FROM Viewers;\n";
        PreparedStatement ViewersSelector = dbconn.prepareStatement(ViewersSelectorString);
        return ViewersSelector.executeQuery();
    }

    protected void BlacklistToken(byte[] token) throws SQLException {
        Timestamp expiry;
        byte[] code;
        try {
            Token tokenValidated = Token.validate(token);
            expiry = tokenValidated.expiry;
            code = tokenValidated.code;
        } catch (InvalidTokenException e) {
            e.printStackTrace();
            return;
        }
        String WriteTokenToBlackListString = "INSERT INTO TokenBlacklist\n" +
                "(tokenCode, expiry)\n" +
                "VALUES(?, ?);\n";
        String DeleteOldTokensString = "DELETE FROM TokenBlacklist\n" +
                "WHERE expiry < NOW();\n";

        PreparedStatement WriteTokenToBlackList = dbconn.prepareStatement(WriteTokenToBlackListString);
        PreparedStatement PurgeOldTokens = dbconn.prepareStatement(DeleteOldTokensString);
        dbconn.setAutoCommit(false);
        try{
            WriteTokenToBlackList.setBytes(1, code);
            WriteTokenToBlackList.setTimestamp(2, expiry);
            WriteTokenToBlackList.executeUpdate();
            PurgeOldTokens.executeUpdate();
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback();
            throw e;
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Check whether a token is currently blacklisted within the database.
     * @param token A byte array of the token.
     * @return true/false whether the token is blacklisted.
     */
    protected boolean IsTokenBlackListed(byte[] token){
        String AttemptTokenLookup = "SELECT *\n" +
                "FROM TokenBlacklist WHERE tokenCode = ?;\n";
        PreparedStatement TokenLookup;
        try {
            TokenLookup = dbconn.prepareStatement(AttemptTokenLookup);
            TokenLookup.setBytes(1, Token.validate(token).code);
        } catch (SQLException | InvalidTokenException e) {
            e.printStackTrace();
            // Invalid tokens can also be seen as blacklisted although they should never get in here
            return true;
        }
        try {
            ResultSet rs = TokenLookup.executeQuery(); // Return whether there is a result or not
            //noinspection UnnecessaryLocalVariable - is there for readability purposes
            boolean IsThereAResult = rs.next();
            return IsThereAResult;
        } catch (SQLException e) {
            return false;
        }
    }
}
