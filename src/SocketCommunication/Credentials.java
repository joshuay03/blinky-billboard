package SocketCommunication;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Crreates a credentials object associated with a user's username and password.
 * Hashes the password.
 * Implements Serrializable.
 * @see Serializable
 */
public class Credentials implements Serializable {

    final private String username; // Username cannot be changed
    private byte[] passwordHash;


    /**
     * CONSTRUCTOR - Generates new credentials object appropriate for transmission over network
     *
     * @param username The username
     * @param password The password to be hashed
     */
    public Credentials(String username, String password) {
        this.username = username;

        // Hash the provided password and store to member
        try {
            this.passwordHash = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a credentials object using an already-hashed password (retrieved from the database)
     * @param username
     * @param passwordHash
     */
    public Credentials(String username, byte[] passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    /**
     * Get the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the user's hashed password
     */
    public byte[] getPasswordHash() {
        return passwordHash;
    }
}
