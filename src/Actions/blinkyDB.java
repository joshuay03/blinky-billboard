package Actions;

import Actions.DBProps;
import Client.User;
import SocketCommunication.Credentials;

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;

public class blinkyDB {
    private DBProps props;
    private Connection dbconn;

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
    // Todo: Make a method which takes a client session object and populates a "db connection" field within it
    public ResultSet getBillboards(/*User user*/) throws SQLException { // First database method - needs permission management
        return dbconn.createStatement().executeQuery("select * from Billboards");
    }

    public ResultSet LookUpUserDetails(String username) throws SQLException {
        PreparedStatement UserLookUp; // Create the prepared statement object
        String userLookUpString = "select * from Users where user_name = ?"; // Define the query to run
        dbconn.setAutoCommit(false);

        UserLookUp = dbconn.prepareStatement(userLookUpString); // Compile the statement

        UserLookUp.setString(1, username); // Insert the provided username into the query

        dbconn.setAutoCommit(true);
        return UserLookUp.executeQuery(); // Run the query
        //rs.next(); // Go to the first result (which should be the only result in this case
    }

    public void AddUser(Server.User user) throws SQLException {
        char[] permissions = new char[4];
        if (user.CanCreateBillboards) permissions[0] = 'B';
        if (user.EditAllBillBoards) permissions[1] = 'E';
        if (user.ScheduleBillboards) permissions[2] = 'S';
        if (user.EditUsers) permissions[3] = 'U';

        PreparedStatement UserInserter;
        String userInsertionString = "INSERT INTO Users\n(user_name, user_permissions, password_hash, salt)\nVALUES(?, ?, ?, ?);";

        dbconn.setAutoCommit(false);

        UserInserter = dbconn.prepareStatement(userInsertionString); // Prepare the insertion statement
        try {
            UserInserter.setString(1, user.credentials.getUsername());
            UserInserter.setString(2, Arrays.toString(permissions));
            UserInserter.setBytes(3, user.credentials.getPasswordHash());
            UserInserter.setBytes(4, user.salt);

            UserInserter.executeUpdate();
            dbconn.commit();
        }
        catch (SQLException e) {
            try {
                dbconn.rollback();
            }
            catch (SQLException excep)
            {
                //System.out.println("Couldn't roll back - " + excep.toString());
            }
        }
        dbconn.setAutoCommit(true);
    }

    public static void main(String[] args) throws IOException, SQLException {
        blinkyDB db = new blinkyDB();
        Credentials creds = new Credentials("Liran", "SeaMonkey123");

        /*String insertionQuery = String.format("INSERT INTO Users\n" +
                "(user_name, user_permissions, password_hash, salt)\n" +
                "VALUES('%s', %s, '%s', '%s');", creds.getUsername(), "NULL", creds.getPasswordHash(), );
        db.dbconn.createStatement().executeQuery(insertionQuery);*/
        //ResultSet rs = db.LookUpUserDetails("Liran");
        //System.out.println(rs.getString("password_hash"));
    }
}
