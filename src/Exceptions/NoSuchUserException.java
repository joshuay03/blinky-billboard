package Exceptions;

/**
 * Exception used if the user does not exist
 */
public class NoSuchUserException extends Exception {
    String username;

    public NoSuchUserException(String username) {
        super(String.format("User '%s' doesn't exist in the specified database.", username));
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
