package Server;

import Exceptions.NoSuchUserException;
import SocketCommunication.Credentials;
import org.mariadb.jdbc.internal.com.read.resultset.UpdatableColumnDefinition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Random;

public class blinkyDB {
    private DBProps props;
    private Connection dbconn;

    /**
     * Database object constructor
     * @throws IOException If db.props isn't found
     * @throws SQLException If there's a problem connecting to the database
     */
    public blinkyDB() throws IOException, SQLException { // Create a new database object - attempting to populate an actual database if one isn't already initialised. Then, start a connection to the database.
        props = new DBProps(); // Read db.props
        // Ensure the schema exists
        Connection init_schema = DriverManager.getConnection("jdbc:mariadb://"+props.url+":3306/", props.username, props.password);
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
    protected ResultSet getBillboards(String searchQuery, String searchType) throws SQLException {
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

    protected ResultSet getBillboards(String searchQuery) throws SQLException{
        return this.getBillboards(searchQuery, "creator");
    }

    protected ResultSet getBillboards() throws SQLException {
        return this.getBillboards(null, null);
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
        //rs.next(); // Go to the first result (which should be the only result in this case
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
            try { // If the insertion failed
                dbconn.rollback();
                throw e;
            }
            catch (SQLException excep)
            { // If the rollback failed
                System.out.println("Couldn't roll back transaction - " + excep.toString());
                throw excep;
            }
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

        byte[] Salted = AuthenticationHandler.HashPasswordHashSalt(user.getCredentials().getPasswordHash(), user.salt);

        dbconn.setAutoCommit(false);
        PreparedStatement updateUser = dbconn.prepareStatement(userUpdateString);
        try{
            updateUser.setString(1, new String(permissions));
            updateUser.setBytes(2, Salted);
            updateUser.setBytes(3, user.salt);
            updateUser.setString(4, user.getCredentials().getUsername());

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
