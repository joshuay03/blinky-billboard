package Server;

import Exceptions.NoSuchUserException;
import Exceptions.UserAlreadyExistsException;
import SocketCommunication.Credentials;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class User {

    private Credentials credentials;
    public Credentials getCredentials() {
        return credentials;
    }
    public byte[] salt;
    public boolean CanCreateBillboards;
    public boolean EditAllBillBoards;
    public boolean ScheduleBillboards;
    public boolean EditUsers;

    /**
     * Creates a new User object based on a username from the supplied database.
     * @param username Username
     * @param database The database
     * @throws NoSuchUserException If the specified username doesn't exist
     */
    public User(String username, blinkyDB database) throws NoSuchUserException {
        try {
            ResultSet userDetails = database.LookUpUserDetails(username);
            userDetails.first(); // There should only ever be one result

            credentials = new Credentials(userDetails.getString("user_name"), userDetails.getBytes("password_hash"));
            salt = userDetails.getBytes("salt");
            String permissions = userDetails.getString("user_permissions");

            CanCreateBillboards = permissions.indexOf('B') != -1;
            EditAllBillBoards = permissions.indexOf('E') != -1;
            ScheduleBillboards = permissions.indexOf('S') != -1;
            EditUsers = permissions.indexOf('U') != -1;
        }
        catch (SQLException e) {
            if (e.getMessage().equals("Current position is after the last row"))
            {
                throw new NoSuchUserException(username);
            }
            else e.printStackTrace();
        }
    }

    /**
     * Registers a new user in the database, then returns it
     * @param credentials The new user's credentials
     * @param CreateBillboards permission
     * @param EditAllBillBoards permission
     * @param ScheduleBillboards permission
     * @param EditUsers permission
     * @param database The database
     * @throws UserAlreadyExistsException If the creation fails - if the user already exists
     */
    public User(Credentials credentials, boolean CreateBillboards, boolean EditAllBillBoards, boolean ScheduleBillboards, boolean EditUsers, blinkyDB database) throws UserAlreadyExistsException {
        try {
            database.RegisterUserInDatabase(credentials, CreateBillboards, EditAllBillBoards, ScheduleBillboards, EditUsers);
        } catch (SQLException e) {
            throw new UserAlreadyExistsException(credentials.getUsername());
        }
        try {
            User user = new User(credentials.getUsername(), database);
            this.EditUsers = user.EditUsers;
            this.ScheduleBillboards = user.ScheduleBillboards;
            this.EditAllBillBoards = user.EditAllBillBoards;
            this.CanCreateBillboards = user.CanCreateBillboards;
            this.credentials = user.credentials;
            this.salt = user.salt;
        } catch (NoSuchUserException ignored) {} // There must be a user since it was just created

    }

    /**
     * Determines whether a given credentials object matches the user
     * @param inputCredentials The credentials to test against
     * @return Whether the password hash matches the user
     */
    public boolean MatchPasswordHash(Credentials inputCredentials){
        byte[] SaltedInput = AuthenticationHandler.HashPasswordHashSalt(inputCredentials.getPasswordHash(), salt);
        //byte[] SaltedUser = AuthenticationHandler.HashPasswordHashSalt(credentials.getPasswordHash(), salt);
        return Arrays.equals(credentials.getPasswordHash(), SaltedInput);
    }

    /** Setters **/
    public void setCredentials(String newPassword, blinkyDB database) throws SQLException { // Change user password. Changing the user's username is not allowed.
        this.credentials = new Credentials(this.credentials.getUsername(), newPassword);
        database.UpdateUserDetails(this);
    }

    public void setSalt(byte[] salt, blinkyDB database) throws SQLException {
        this.salt = salt;
        database.UpdateUserDetails(this);
    }

    public void setCanCreateBillboards(boolean canCreateBillboards, blinkyDB database) throws SQLException {
        CanCreateBillboards = canCreateBillboards;
        database.UpdateUserDetails(this);
    }

    public void setEditAllBillBoards(boolean editAllBillBoards, blinkyDB database) throws SQLException {
        EditAllBillBoards = editAllBillBoards;
        database.UpdateUserDetails(this);
    }

    public void setScheduleBillboards(boolean scheduleBillboards, blinkyDB database) throws SQLException {
        ScheduleBillboards = scheduleBillboards;
        database.UpdateUserDetails(this);
    }

    public void setEditUsers(boolean editUsers, blinkyDB database) throws SQLException {
        EditUsers = editUsers;
        database.UpdateUserDetails(this);
    }
}
