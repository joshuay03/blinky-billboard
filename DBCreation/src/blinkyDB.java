import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class blinkyDB {
    private DBProps props;
    public Connection dbconn;

    public blinkyDB() throws IOException, SQLException { // Create a new database object - attempting to populate an actual database if one isn't already initialised. Otherwise, start a connection to a database.
        props = new DBProps(); // Read db.props
        String sqldump = new File("blinkybillboard.sql").getPath();
        try { // DB creation code
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(new String[]{"/bin/bash", "-c", "mariaDB -p",props.password, " -u ", props.username, " -h ", props.url , " ", props.schema," < " + sqldump});
            pr.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.toString());
        }
        // Start a database connection
        dbconn = DriverManager.getConnection("jdbc:mariadb://"+props.url+":3306/"+props.schema, props.username, props.password);
    }
}
