import java.io.File;
import java.io.IOException;

public class DBCreator {
    public static void main(String[] args) throws IOException {
        File file = new File("blinkybillboard.sql");
        DBProps props = new DBProps();
        String path = file.getPath();

        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(new String[]{"/bin/bash", "-c", "mariaDB -p",props.password, " -u ", props.username, " -h ", props.url , " ", props.schema," < " + path});
            pr.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.toString());
        }
    }
}