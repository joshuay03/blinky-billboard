import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
