import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

class blinkyDBTest {
    public static void main(String[] args) throws IOException, SQLException {
        blinkyDB db = new blinkyDB();
        System.out.println(db.getBillboards());
    }
}