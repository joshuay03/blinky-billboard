package Server;

import Exceptions.NoSuchUserException;
import SocketCommunication.Credentials;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;

public class AuthenticationHandler {

    /**
     * Takes a password hash, a salt, and returns the hash of both.
     * @param passwordHash Hash of the password
     * @param salt A random 100 byte array
     * @return The hash of the concatenation of the password hash + salt
     * WARNING - Assumes that the SHA-256 algorithm is available.
     */
    public static byte[] HashPasswordHashSalt(byte[] passwordHash, byte[] salt) {
        try {
            byte[] saltedPasswordHash = new byte[salt.length + passwordHash.length];
            System.arraycopy(salt, 0, saltedPasswordHash, 0, salt.length);
            System.arraycopy(passwordHash, 0, saltedPasswordHash, salt.length, passwordHash.length);
            return MessageDigest.getInstance("SHA-256").digest(saltedPasswordHash);
        }
        catch (NoSuchAlgorithmException e)
        {
            return null;
        }
    }

    /**
     * Takes a set of credentials and the database, and checks if they match. If the user doesn't exist - they don't match
     * @param credentials The user credentials to authenticate
     * @param database The database
     * @return Whether the credentials match
     */

    public static boolean Authenticate (Credentials credentials, blinkyDB database) {
        

        try {
            // Tries to get the user's details from the database
            User user = new User(credentials.getUsername(), database);
        // If the user does exist ...
        // Concatenate the password and the salt
        // Hash the result
        // If the result of the password + salt hash matches the content of the database, return true
            return user.MatchPasswordHash(credentials);

        } catch (NoSuchUserException e) { // If user does not exist
            return false;
        }
    }


}
