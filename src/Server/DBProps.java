package Server;

import java.io.*;
import java.util.Properties;

public class DBProps {
    public String url;
    public String schema;
    public String username;
    public String password;

    public DBProps() throws IOException {

        FileReader propsFile = new FileReader("db.props");
        Properties props = new Properties();
        props.load(propsFile);
        this.url = props.getProperty("url");
        this.username = props.getProperty("username");
        this.password = props.getProperty("password");
        this.schema = props.getProperty("schema");
    }
}