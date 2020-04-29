import java.io.File;
import java.io.IOException;

public class DBCreator {
    public static void main(String[] args) {
        File file = new File("blinkybillboard.sql");
        String path = file.getPath();

        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(new String[]{"/bin/bash", "-c", "mariaDB -ppassword -u root -h localhost testDB < " + path});
            pr.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.toString());
        }
    }
}