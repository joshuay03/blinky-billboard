package SocketCommunication;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Credentials implements Serializable {

    final private String username; // Username cannot be changed
    private byte[] passwordHash;


    public String getUsername() {
        return username;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    /** CONSTRUCTOR - Generates new credentials object appropriate for transmission over network
     *
     * @param username
     * @param password
     */
    public Credentials (String username, String password){
        this.username = username;

        // Hash the provided password and store to member
        try {
            this.passwordHash = MessageDigest.getInstance("SHA-256").digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public Credentials(String username, byte[] passwordHash){ // Create a credentials object using an already-hashed password (retrieved from the database)
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public static void main (String[] args){
        Credentials credentials = new Credentials("Liran", "Seamonkeys123");

        /* This writes the entire object to a file as a byte[] for read back later
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("test");
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);

            out.writeObject(credentials);

            out.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // This reads in a Credentials object which was formerly stored in a file as a byte[]
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("test"));

            Credentials credentials1 = (Credentials)in.readObject();

            in.close();

        } catch (Exception e) {

        }*/

    }
}
