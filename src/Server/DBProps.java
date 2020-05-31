package Server;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * This class reads in a given properties file.
 * Information within the file is stored within this object,
 * and is used when instantiating a new database.
 */
public class DBProps {
    public String url;
    public String schema;
    public String username;
    public String password;

    /**
     * Constructor for a DBProps object, which overrides schema.
     * @param overrideSchema the new schema for the database
     * @throws IOException
     */
    public DBProps(String overrideSchema) throws IOException {
        FileReader propsFile = new FileReader("db.props");
        Properties props = new Properties();
        props.load(propsFile);
        this.url = props.getProperty("url");
        this.username = props.getProperty("username");
        this.password = props.getProperty("password");
        if (overrideSchema != null) this.schema = overrideSchema;
        else this.schema = props.getProperty("schema");
    }

    /**
     * Constructor for a DBProps object.
     * @throws IOException
     */
    public DBProps() throws IOException {
        DBProps readprops = new DBProps(null);
        this.url = readprops.url;
        this.username = readprops.username;
        this.password = readprops.password;
        this.schema = readprops.schema;
    }
}