package Client;

public class ControlPanel {
    public static void main(String[] args) {
        /* creating the db only use when db not created*/
//        DBCreator create = new DBCreator();
//        create.CreateDB();
        ClientUser user = new ClientUser(new String[]{"Create Billboards", "List Billboards", "Schedule Billboards", "Edit Users"});
       

    }
}
