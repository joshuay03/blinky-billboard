import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBCreator {
    private Connection con;
    public static void main(String args[]) throws SQLException {
        String pass = "1";
        java.sql.Connection con = DriverManager.getConnection("jdbc:mariadb://192.168.0.191:3306/testDB", "liranTest", pass);

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("drop table table1;");

        System.out.println(rs);

        st.close();

        con.close();

    }
}