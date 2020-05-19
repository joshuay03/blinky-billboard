package Server;

import Exceptions.InvalidTokenException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class Token implements Serializable {
    String username;
    Timestamp expiryDate;

    private Token(String username, Timestamp expiry){
        this.expiryDate = expiry;
        this.username = username;
    }

    Token(String username){
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        this.expiryDate = Timestamp.valueOf(tomorrow); // Generate an expiry date
        this.username = username;
    }

    private static SecretKey getKey(String filePath){
        // Read the secretKey from a file and create an AES key based on it
        String encodedKey;
        try {
            encodedKey = Files.readString(Paths.get(filePath));
        } catch (IOException e) { // If the tokenSecretKey file wasn't found
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
        byte[] decodedKey = null;
        try {
            // Hash the secretKey to ensure that it has an acceptable length
            decodedKey = MessageDigest.getInstance("SHA-256").digest(encodedKey.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert decodedKey != null;
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * Takes a username and generates a token for it
     * @param username The username to generate a token for
     * @return The token
     */
    public static byte[] Generate(String username) {
        // Pad the username up to the maximum length, to ensure the username's length cannot be determined based on the token's length
        int MaxUserNameLength = 100;
        // Pad the username up to the maximum length with newlines (since they can't be in the username)
        String paddedUsername = Optional.of(MaxUserNameLength - username.length())
                .filter(i -> i > 0)
                .map(i-> String.format("%" + i + "s", "").replace(" ", String.valueOf('\n')) + username)
                .orElse(username);
        // Generate a new token object
        Token unencrypted = new Token(paddedUsername);
        // Turn it to a byte[] for encryption
        ByteArrayOutputStream serialiser = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(serialiser);
            oos.writeObject(unencrypted);
            oos.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
        byte[] unencryptedSerialised = serialiser.toByteArray();
        SecretKey key = getKey("tokenSecretKey");
        Cipher c = null;
        try {
            c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            assert c != null;
            c.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] encryptedOutput = null;
        // Return the encrypted token object
        try {
            encryptedOutput = c.doFinal(unencryptedSerialised);
        } catch (IllegalBlockSizeException | BadPaddingException e) { // These exceptions should never actually occur
            e.printStackTrace();
        }
        return encryptedOutput;
    }

    /**
     * Takes a token and returns a readable object, provided that the token is valid
     * @param encryptedToken The token to validate and read the details of
     * @return The token's details
     * @throws InvalidTokenException If the provided token is invalid
     */
    public static Token readToken(byte[] encryptedToken) throws InvalidTokenException {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }  // Should never be thrown
        SecretKey key = getKey("tokenSecretKey");
        try {
            assert cipher != null;
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] decryptedToken = null;
        try {
            decryptedToken = cipher.doFinal(encryptedToken);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }  // Should never be thrown
        ObjectInputStream ois = null;
        try {
            assert decryptedToken != null;
            ByteArrayInputStream is = new ByteArrayInputStream(decryptedToken);
            ois = new ObjectInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Token token = null;
        try {
            assert ois != null;
            token = (Token)ois.readObject();
        } catch (IOException | ClassNotFoundException e) { // These exceptions should never be thrown
            e.printStackTrace();
        } catch(NullPointerException e){
            // If the token is invalid, then reading the object from the decrypted token will fail.
            throw new InvalidTokenException(encryptedToken);
        }
        assert token != null;
        return new Token(token.username.replaceAll(String.valueOf('\n'), ""), token.expiryDate);
    }
}
