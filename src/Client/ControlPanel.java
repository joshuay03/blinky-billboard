package Client;

import Actions.DBCreator;

public class ControlPanel {
    public static void main(String[] args) {
        /* creating the db only use when db not created*/
//        DBCreator create = new DBCreator();
//        create.CreateDB();
        User user = new User(new String[]{"Create Billboards", "List Billboards", "Schedule Billboards", "Edit Users"});
        Login loginPanel = new Login(user);

    }
}