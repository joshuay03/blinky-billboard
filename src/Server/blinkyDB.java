package Server;

import BillboardSupport.Billboard;
import BillboardSupport.DummyBillboards;
import BillboardSupport.Schedule;
import Exceptions.BillboardAlreadyExistsException;
import Exceptions.BillboardNotFoundException;
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
        Connection init_schema = DriverManager.getConnection("jdbc:mariadb://" + props.url, props.username, props.password);
        if (dropSchema) init_schema.createStatement().executeQuery("DROP DATABASE IF EXISTS " + props.schema);
        init_schema.createStatement().executeQuery("CREATE DATABASE IF NOT EXISTS " + props.schema);
        init_schema.close();
        // Start a database connection
        dbconn = DriverManager.getConnection("jdbc:mariadb://" + props.url + "/" + props.schema, props.username, props.password);
        // Try to initialise based on sql file
        Path sqlInitFile = Paths.get(new File("blinkybillboard.sql").getPath());
        String[] batch = new String(Files.readAllBytes(sqlInitFile)).split("(?<=;)");
        // Execute all statements in the array
        for (String toExec : batch) {
            if (!toExec.trim().isEmpty()) // (don't execute empty statements)
                dbconn.createStatement().executeQuery(toExec);
        }
    }

    public blinkyDB() throws IOException, SQLException {
        blinkyDB newDB = new blinkyDB(false, null);
        this.dbconn = newDB.dbconn;
        this.props = newDB.props;
    }

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

    public List<Billboard> getBillboards() throws SQLException {
        return this.getBillboards(null, null);
    }

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
            // Takes a property retriever for Billboards, and applies it to either the given billboard, or a default billboard object
            Function<Function<Billboard, Object>, Object> getPropertySafely = (Function<Billboard, Object> m) ->
                    Objects.requireNonNullElse(m.apply(billboard_in), m.apply(DummyBillboards.defaultBillboard()));
            String BillboardInsertQuery = "INSERT INTO Billboards\n" +
                    "(billboard_name, creator, backgroundColour, messageColour, informationColour, message, information, billboardImage)\n" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?);\n";
            dbconn.setAutoCommit(false);
            byte[] SerialisedImage;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                new ObjectOutputStream(bos).writeObject(getPropertySafely.apply(Billboard::getBillboardImage));
                SerialisedImage = bos.toByteArray();
            } catch (IOException e) {
                SerialisedImage = new byte[0];
            }
            PreparedStatement insertBillboard = dbconn.prepareStatement(BillboardInsertQuery);
            try {
                insertBillboard.setString(1, billboard_in.getBillboardName()); // This is only okay because I require the submitted billboard to have a name
                insertBillboard.setString(2, creator);
                insertBillboard.setInt(3, ((Color) getPropertySafely.apply(Billboard::getBackgroundColour)).getRGB());
                insertBillboard.setInt(4, ((Color) getPropertySafely.apply(Billboard::getMessageColour)).getRGB());
                insertBillboard.setInt(5, ((Color) getPropertySafely.apply(Billboard::getInformationColour)).getRGB());
                insertBillboard.setString(6, ((String) getPropertySafely.apply(Billboard::getMessage)));
                insertBillboard.setString(7, ((String) getPropertySafely.apply(Billboard::getInformation)));
                insertBillboard.setBytes(8, SerialisedImage);
                insertBillboard.executeUpdate();
                dbconn.commit();
            } catch (SQLException e) {
                dbconn.rollback();
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
                ScheduleList.add(new Schedule(startTime, duration, repeatInterval, billboardName));
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
                ScheduleList.add(new Schedule(startTime, duration, repeatInterval, billboardName));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ScheduleList.get(0);
    }

    /**
     * Takes a billboard, and assigns a given schedule to it
     *
     * @param billboard The billboard
     * @param schedule  The schedule to assign to the billboard
     */
    public void ScheduleBillboard(Billboard billboard, Schedule schedule) throws SQLException {
        String SchedulingString = "INSERT INTO blinkyBillboard.Scheduling\n" +
                "(billboard_name, viewer_id, start_time, duration, `interval`)\n" +
                "VALUES(?, ?, ?, ?, ?);\n";

        PreparedStatement CreateSchedule;
        dbconn.setAutoCommit(false);
        try {
            CreateSchedule = dbconn.prepareStatement(SchedulingString);

            CreateSchedule.setString(1, billboard.getBillboardName());
            CreateSchedule.setInt(2, 1);
            CreateSchedule.setTimestamp(3, schedule.StartTime);
            CreateSchedule.setInt(4, schedule.duration);
            CreateSchedule.setInt(5, schedule.repeatInterval);

            CreateSchedule.executeUpdate();
            dbconn.commit();

        } catch (SQLException e) {
            dbconn.setAutoCommit(true);
            dbconn.rollback();
        }

        billboard.setSchedule(schedule);
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
                "WHERE user_name='?';\n";

        PreparedStatement UserDeleter = dbconn.prepareStatement(UserDeletionString);

        dbconn.setAutoCommit(false);
        try {
            UserDeleter.setString(1, username);

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
    public void UnscheduleBillboard(String name) throws SQLException {
        String SchedulesDeletionString = "DELETE FROM Scheduling\n" +
                "billboard_name=?;\n";

        PreparedStatement SchedulesDeleter = dbconn.prepareStatement(SchedulesDeletionString);
        dbconn.setAutoCommit(false);
        try {
            SchedulesDeleter.setString(1, name);
            SchedulesDeleter.executeUpdate();
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
    public void DeleteBillboard(String name) throws SQLException {
        String BillboardDeletionString = "DELETE FROM Billboards\n" +
                "WHERE billboard_name=?;\n";
        String SchedulesDeletionString = "DELETE FROM Scheduling\n" +
                "billboard_name=?;\n";
        PreparedStatement BillboardDeleter = dbconn.prepareStatement(BillboardDeletionString);
        PreparedStatement SchedulesDeleter = dbconn.prepareStatement(SchedulesDeletionString);
        dbconn.setAutoCommit(false);
        try {
            SchedulesDeleter.setString(1, name);
            BillboardDeleter.setString(1, name);
            SchedulesDeleter.executeUpdate();
            BillboardDeleter.executeUpdate();
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback();
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
