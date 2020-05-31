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

    // TODO - create wrapper functions for readability
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

    /**
     * Creates a user object without registering it. Used in the request builder.
     * @param credentials Username and hashed password
     * @param CreateBillboards permission
     * @param EditAllBillBoards permission
     * @param ScheduleBillboards permission
     * @param EditUsers permission
     */
    public User(Credentials credentials, boolean CreateBillboards, boolean EditAllBillBoards, boolean ScheduleBillboards, boolean EditUsers){
        this.saltedCredentials = credentials;
        this.CanCreateBillboards = CreateBillboards;
        this.EditAllBillBoards = EditAllBillBoards;
        this.ScheduleBillboards = ScheduleBillboards;
        this.EditUsers = EditUsers;
    }

    /**
     * @return the salted credentials
     */
    public Credentials getSaltedCredentials() {
        return saltedCredentials;
    }

    /**
     * @return the salted credentials
     */
    public boolean CanCreateBillboards() {
        return CanCreateBillboards;
    }

    /**
     * @return whether the user can edit the billboards
     */
    public boolean CanEditAllBillboards() {
        return EditAllBillBoards;
    }

    /**
     * @return whether the user can schedule billboards
     */
    public boolean CanScheduleBillboards() {
        return ScheduleBillboards;
    }

    /**
     * @return whether the user can edit users
     */
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
     * Change the user's password given a new credentials object.
     * @param newCredentials new credentials assigned to the user
     * @param database
     */
    public void setPasswordFromCredentials(Credentials newCredentials, blinkyDB database) { // Change user password. Changing the user's username is not allowed.
        this.saltedCredentials = new Credentials(this.saltedCredentials.getUsername(), AuthenticationHandler.HashPasswordHashSalt(newCredentials.getPasswordHash(), salt));
    }

    /**
     * Set the salt given a byte array.
     * @param salt byte array of salt
     */
    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    /**
     * Set the permission of the user
     * @param canCreateBillboards true/false
     */
    public void setCanCreateBillboards(boolean canCreateBillboards) {
        CanCreateBillboards = canCreateBillboards;
    }

    /**
     * Set the permission of the user
     * @param editAllBillBoards true/false
     */
    public void setEditAllBillBoards(boolean editAllBillBoards) {
        EditAllBillBoards = editAllBillBoards;
    }

    /**
     * Set the permission of the user
     * @param scheduleBillboards true/false
     */
    public void setScheduleBillboards(boolean scheduleBillboards) {
        ScheduleBillboards = scheduleBillboards;
    }

    /**
     * Set the permission of the user
     * @param editUsers true/false
     */
    public void setEditUsers(boolean editUsers) {
        EditUsers = editUsers;
    }
}
