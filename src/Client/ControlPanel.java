package Client;

public class ControlPanel {
    public static void main(String[] args) {
        User user = new User(new String[]{"Create Billboards", "List Billboards", "Schedule Billboards", "Edit Users"});
        Login loginPanel = new Login(user);
    }
}