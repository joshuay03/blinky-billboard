import java.io.IOException;

public class blinkyDB {
    private DBProps props;
    public blinkyDB() throws IOException { // Create a new database object - attempting to populate an actual database if one isn't already initialised. Otherwise, start a connection to a database.
        props = new DBProps(); // Read db.props
    }
    public static void main(String[] args){

    }
}
