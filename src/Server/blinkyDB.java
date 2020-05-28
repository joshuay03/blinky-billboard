package Server;

import BillboardSupport.Billboard;
import BillboardSupport.DummyBillboards;
import SocketCommunication.Credentials;
import SocketCommunication.Response;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

public class blinkyDB {
    private DBProps props;
    private Connection dbconn;

    /**
     * Database object constructor
     * @throws IOException If db.props isn't found
     * @throws SQLException If there's a problem connecting to the database
     */
    public blinkyDB(Boolean dropSchema) throws IOException, SQLException { // Create a new database object - attempting to populate an actual database if one isn't already initialised. Then, start a connection to the database.
        props = new DBProps(); // Read db.props
        // Ensure the schema exists
        Connection init_schema = DriverManager.getConnection("jdbc:mariadb://"+props.url+":3306/", props.username, props.password);
        if (dropSchema) init_schema.createStatement().executeQuery("DROP DATABASE IF EXISTS " + props.schema);
        init_schema.createStatement().executeQuery("CREATE DATABASE IF NOT EXISTS " + props.schema);
        init_schema.close();
        // Start a database connection
        dbconn = DriverManager.getConnection("jdbc:mariadb://"+props.url+":3306/"+props.schema, props.username, props.password);
        // Try to initialise based on sql file
        Path sqlInitFile = Paths.get(new File("blinkybillboard.sql").getPath());
        String[] batch = new String(Files.readAllBytes(sqlInitFile)).split("(?<=;)");
            // Execute all statements in the array
        for (String toExec:batch) {
            if (!toExec.trim().isEmpty()) // (don't execute empty statements)
            dbconn.createStatement().executeQuery(toExec);
        }
    }

    public blinkyDB() throws IOException, SQLException {
        blinkyDB newDB = new blinkyDB(false);
        this.dbconn = newDB.dbconn;
        this.props = newDB.props;
    }

    public ResultSet getBillboards(String searchQuery, String searchType) throws SQLException {
        PreparedStatement getBillboards;
        final String billboardLookUpString = (searchQuery != null && searchType != null) ?
                "select * from Billboards where ? like \"%?%\"" : "select * from Billboards";
        dbconn.setAutoCommit(false);
        getBillboards = dbconn.prepareStatement(billboardLookUpString);
        if (searchQuery != null && searchType != null)
        {
            getBillboards.setString(1, searchType);
            getBillboards.setString(2, searchQuery);
        }
        dbconn.setAutoCommit(true);
        return getBillboards.executeQuery();
    }

    public ResultSet getBillboards(String searchQuery) throws SQLException{
        return this.getBillboards(searchQuery, "creator");
    }

    public ResultSet getBillboards() throws SQLException {
        return this.getBillboards(null, null);
    }

    public void CreateViewer(String socket) throws SQLException {
        String ViewerCreationString = "INSERT INTO blinkyBillboard.Viewers\n" +
                "(socket)\n" +
                "VALUES(?);\n";
        PreparedStatement ViewerInserter = dbconn.prepareStatement(ViewerCreationString);
        dbconn.setAutoCommit(false);
        ViewerInserter.setString(1, socket);
        ViewerInserter.executeUpdate();
        try{dbconn.commit();}
        catch (SQLException e){
            dbconn.rollback();
        }
        dbconn.setAutoCommit(true);
    }

    public void createBillboard(Billboard billboard_in, String creator) throws SQLException {
        assert creator != null;
        // Takes a property retriever for Billboards, and applies it to either the given billboard, or a default billboard object
        Function<Function<Billboard, Object>, Object> getPropertySafely = (Function<Billboard, Object> m) ->
                Objects.requireNonNullElse(m.apply(billboard_in), m.apply(DummyBillboards.defaultBillboard()));
        String BillboardInsertQuery = "INSERT INTO Billboards\n" +
                "(creator, backgroundColour, messageColour, informationColour, message, information, billboardImage)\n" +
                "VALUES(?, ?, ?, ?, ?, ?, ?);\n";
        dbconn.setAutoCommit(false);
        byte[] SerialisedImage;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(bos).writeObject(getPropertySafely.apply(Billboard::getBillboardImage));
            SerialisedImage = bos.toByteArray();
        } catch (IOException e) { SerialisedImage = new byte[0]; }
        PreparedStatement insertBillboard = dbconn.prepareStatement(BillboardInsertQuery);
        try {
            insertBillboard.setString(1, creator);
            insertBillboard.setInt(2, ((Color)getPropertySafely.apply(Billboard::getBackgroundColour)).getRGB());
            insertBillboard.setInt(3, ((Color)getPropertySafely.apply(Billboard::getMessageColour)).getRGB());
            insertBillboard.setInt(4, ((Color)getPropertySafely.apply(Billboard::getInformationColour)).getRGB());
            insertBillboard.setString(5, ((String)getPropertySafely.apply(Billboard::getMessage)));
            insertBillboard.setString(6, ((String)getPropertySafely.apply(Billboard::getInformation)));
            insertBillboard.setBytes(7, SerialisedImage);
            insertBillboard.executeUpdate();
            dbconn.commit();
        } catch (SQLException e) {
            dbconn.rollback();
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Custom lookup function for server admins - capable of retrieving any data within the database.
     * @param tableName Name of table in which the lookup should be performed
     * @param columnFilter Name of column to filter by
     * @param valueFilter Value to filter by - supports *
     * @return The result of the query
     * @throws SQLException If there's an error in the query
     */
    protected ResultSet AdminLookUp(String tableName, String columnFilter, String valueFilter) throws SQLException {
        final String sqlfilter = (!valueFilter.equals("*")) ? String.format("where %s = %s", columnFilter, valueFilter) : "";
        return dbconn.createStatement().executeQuery(String.format("select * from %s %s", tableName, sqlfilter));
    }

    /**
     * A method for the User constructor to read user data from the database
     * @param username Username
     * @return The details of said user
     * @throws SQLException If there are no users with the given username
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

    public ResultSet getSchedules(LocalDateTime time) throws SQLException {
        String scheduleLookup = "SELECT * FROM Scheduling WHERE start_time < ?";
        PreparedStatement ScheduleLookUp;
        dbconn.setAutoCommit(false);
        ScheduleLookUp = dbconn.prepareStatement(scheduleLookup);
        ScheduleLookUp.setTimestamp(1, Timestamp.valueOf(time));
        dbconn.setAutoCommit(true);
        return ScheduleLookUp.executeQuery();
    }

    /**
     * This method takes new user details and adds them to the database.
     * @param credentials The new user's credentials
     * @param CanCreateBillboards permission
     * @param EditAllBillBoards permission
     * @param ScheduleBillboards permission
     * @param EditUsers permission
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
        }
        catch (SQLException e) {
            dbconn.rollback(); // Try to rollback
            throw e;
        }
        dbconn.setAutoCommit(true);
    }

    /**
     * Updates user details
     * @param user User object to update the details of
     * @throws SQLException when the update fails
     */
    protected void UpdateUserDetails(User user) throws SQLException {
        char[] permissions = new char[4];
        if (user.CanCreateBillboards) permissions[0] = 'B';
        if (user.EditAllBillBoards) permissions[1] = 'E';
        if (user.ScheduleBillboards) permissions[2] = 'S';
        if (user.EditUsers) permissions[3] = 'U';

        String userUpdateString = "UPDATE Users\n" +
                "SET user_permissions=?, password_hash=?, salt=?\n" +
                "WHERE user_name=?;";

        dbconn.setAutoCommit(false);
        PreparedStatement updateUser = dbconn.prepareStatement(userUpdateString);
        try{
            updateUser.setString(1, new String(permissions));
            updateUser.setBytes(2, user.getSaltedCredentials().getPasswordHash());
            updateUser.setBytes(3, user.salt);
            updateUser.setString(4, user.getSaltedCredentials().getUsername());

            updateUser.executeUpdate();
            dbconn.commit();
        }
        catch (SQLException e){
            try{
                dbconn.rollback();
                throw e;
            }
            catch (SQLException excep){
                System.out.println(String.format("Rollback failed - %s", excep.getMessage()));
                throw excep;
            }
        }
        dbconn.setAutoCommit(true);
    }
}
