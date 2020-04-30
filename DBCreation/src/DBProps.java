import java.io.*;
import java.util.Properties;

public class DBProps {
    private String url;
    private String schema;
    private String username;
    private String password;

    DBProps() throws IOException {

        FileReader propsFile = new FileReader("./db.props");
        Properties props = new Properties();
        props.load(propsFile);
        this.url = props.getProperty("url");
        this.username = props.getProperty("username");
        this.password = props.getProperty("password");
        this.schema = props.getProperty("schema");
    }
}
