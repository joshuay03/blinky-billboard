package Actions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DBCreator {
    public void CreateDB(){
        File file = new File("blinkybillboard.sql");
        DBProps props = null;
        try {
            props = new DBProps();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = file.getPath();
        // Create schema if it doesn't exist

        try {
            Runtime rt = Runtime.getRuntime();
            String dbInit = String.format("mariaDB -p%s -u %s -h %s %s < %s", props.password, props.username, props.url, props.schema, path);
            String[] command = new String[]{"/bin/bash", "-c", dbInit};
            Process pr = rt.exec(command);
            pr.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.toString());
        }
    }
}