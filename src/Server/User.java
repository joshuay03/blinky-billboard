package Server;

import Exceptions.NoSuchUserException;
import Exceptions.UserAlreadyExistsException;
import SocketCommunication.Credentials;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class User implements Serializable {

    public byte[] salt;
    private Credentials saltedCredentials;
    private boolean CanCreateBillboards;
    private boolean EditAllBillBoards;
    private boolean ScheduleBillboards;
    private boolean EditUsers;
    /**
     * Creates a new User object based on a username from the supplied database.
     *
     * @param username Username
     * @param database The database
     * @throws NoSuchUserException If the specified username doesn't exist
     */
    public User(String username, blinkyDB database) throws NoSuchUserException {
        try {
            ResultSet userDetails = database.LookUpUserDetails(username);
            userDetails.first(); // There should only ever be one result

            saltedCredentials = new Credentials(userDetails.getString("user_name"), userDetails.getBytes("password_hash"));
            salt = userDetails.getBytes("salt");
            String permissions = userDetails.getString("user_permissions");

            CanCreateBillboards = permissions.indexOf('B') != -1;
            EditAllBillBoards = permissions.indexOf('E') != -1;
            ScheduleBillboards = permissions.indexOf('S') != -1;
            EditUsers = permissions.indexOf('U') != -1;
        } catch (SQLException e) {
            if (!e.getMessage().equals("Current position is after the last row")) {
                e.printStackTrace();
            }
            throw new NoSuchUserException(username);
        }
    }

    /**
     * Registers a new user in the database, then returns it
     *
     * @param saltedCredentials  The new user's credentials
     * @param CreateBillboards   permission
     * @param EditAllBillBoards  permission
     * @param ScheduleBillboards permission
     * @param EditUsers          permission
     * @param database           The database
     * @throws UserAlreadyExistsException If the creation fails - if the user already exists
     */
    public User(Credentials saltedCredentials, boolean CreateBillboards, boolean EditAllBillBoards, boolean ScheduleBillboards, boolean EditUsers, blinkyDB database) throws UserAlreadyExistsException {
        try {
            database.RegisterUserInDatabase(saltedCredentials, CreateBillboards, EditAllBillBoards, ScheduleBillboards, EditUsers);
        } catch (SQLException e) {
            throw new UserAlreadyExistsException(saltedCredentials.getUsername());
        }
        User user = null;
        try {
            user = new User(saltedCredentials.getUsername(), database);
        } catch (NoSuchUserException ignored) {
        } // There must be a user since it was just created
        assert user != null;
        this.EditUsers = user.EditUsers;
        this.ScheduleBillboards = user.ScheduleBillboards;
        this.EditAllBillBoards = user.EditAllBillBoards;
        this.CanCreateBillboards = user.CanCreateBillboards;
        this.saltedCredentials = user.saltedCredentials;
        this.salt = user.salt;
    }

    public Credentials getSaltedCredentials() {
        return saltedCredentials;
    }

    public boolean CanCreateBillboards() {
        return CanCreateBillboards;
    }

    public boolean CanEditAllBillboards() {
        return EditAllBillBoards;
    }

    public boolean CanScheduleBillboards() {
        return CanCreateBillboards;
    }

    public boolean CanEditUsers() {
        return EditUsers;
    }

    /**
     * Determines whether a given credentials object matches the user
     *
     * @param inputCredentials The credentials to test against
     * @return Whether the password hash matches the user
     */
    public boolean MatchUnsaltedCredentials(Credentials inputCredentials) {
        byte[] SaltedInput = AuthenticationHandler.HashPasswordHashSalt(inputCredentials.getPasswordHash(), salt);
        // The user's credentials are already salted and rehashed
        return Arrays.equals(saltedCredentials.getPasswordHash(), SaltedInput);
    }

    /**
     * Setters
     **/
    public void setPasswordFromCredentials(Credentials newCredentials, blinkyDB database) { // Change user password. Changing the user's username is not allowed.
        this.saltedCredentials = new Credentials(this.saltedCredentials.getUsername(), AuthenticationHandler.HashPasswordHashSalt(newCredentials.getPasswordHash(), salt));
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public void setCanCreateBillboards(boolean canCreateBillboards) {
        CanCreateBillboards = canCreateBillboards;
    }

    public void setEditAllBillBoards(boolean editAllBillBoards) {
        EditAllBillBoards = editAllBillBoards;
    }

    public void setScheduleBillboards(boolean scheduleBillboards) {
        ScheduleBillboards = scheduleBillboards;
    }

    public void setEditUsers(boolean editUsers) {
        EditUsers = editUsers;
    }
}
