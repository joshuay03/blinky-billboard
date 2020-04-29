import java.io.IOException;
import java.sql.SQLException;

public class DBCreator {
    public static void main(String args[]) throws SQLException {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec(new String[]{"/bin/bash", "-c", "mariaDB -ppassword -u root -h localhost testDB < ../../blinkybillboard.sql"});
            pr.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.toString());
        }
    }
}