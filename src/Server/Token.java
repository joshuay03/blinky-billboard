package Server;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Token implements Serializable {
    String username;
    Timestamp expiryDate;

    Token(String username){
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        this.expiryDate = Timestamp.valueOf(tomorrow); // Generate an expiry date
        this.username = username;
    }

    private Token(String username, Timestamp expiry){
        this.expiryDate = expiry;
        this.username = username;
    }

    private static SecretKey getKey(String filePath){
        // Read the secretKey from a file and create an AES key based on it
        String encodedKey = null;
        try {
            encodedKey = Files.readString(Paths.get(filePath));
        } catch (IOException e) { // If the tokenEncryptionKey file wasn't found
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
        byte[] decodedKey = encodedKey.getBytes();
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }


    /**
     * Takes a username and generates a token for it
     * @param username The username to generate a token for
     * @return The token
     * @throws BadPaddingException To be handled
     * @throws IllegalBlockSizeException To be handled
     */
    public static byte[] Generate(String username) throws BadPaddingException, IllegalBlockSizeException {
        // Generate a new token object
        Token unencrypted = new Token(username);
        // Turn it to a byte[] for encryption
        ByteArrayOutputStream serialiser = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(serialiser);
            oos.writeObject(unencrypted);
            oos.flush();
        } catch (IOException ignored){}
        byte[] unencryptedSerialised = serialiser.toByteArray();
        SecretKey key = getKey("tokenEncryptionKey");
        Cipher c = null;
        try {
            c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException ignored) {} catch (NoSuchPaddingException ignored) {}
        try {
            c.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        // Return the encrypted token object
        return c.doFinal(unencryptedSerialised);
    }

    /**
     * Takes a token and returns a readable object if it's valid - throws an exception if the token is invalid
     * @param encryptedToken The token to read
     * @return A readable token object
     * @throws NoSuchPaddingException To be handled
     * @throws NoSuchAlgorithmException To be handled
     */
    public static Token readToken(byte[] encryptedToken) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        SecretKey key = getKey("tokenEncryptionKey");
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] decryptedToken = null;
        decryptedToken = cipher.doFinal(encryptedToken);
        ObjectInputStream ois = null;
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(decryptedToken);
            ois = new ObjectInputStream(is);
        } catch (IOException ignored) {}
        Token token = null;
        try {
            token = (Token)ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ignored) {} catch(NullPointerException e){
            e.printStackTrace();
        }
        return token;
    }

    public static void main(String[] args) throws BadPaddingException, IllegalBlockSizeException {
        byte[] test = Generate("Liran");
        System.out.println(test.toString());
    }
}
