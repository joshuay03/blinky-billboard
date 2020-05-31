package SocketCommunication;

import BillboardSupport.Billboard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Represents the response object sent back to the client per request made by the client.
 * Has a status - true or false - representing whether the request was made successfully.
 * Returns some data of type object, typically a string or a billboard object.
 * @see Billboard
 * Implements the Serializable class.
 * @see Serializable
 */
public class Response implements Serializable {
    // pass/fail
    private boolean status;
    // generic data object for passing data back to client
    private Object data;

    /**
     * Constructor for a new response object
     * @param status true/false value depending if the request made by the client was successful or not
     * @param data any data type permissible. Generally a string or a billboard object.
     */
    public Response(boolean status, Object data) {
        this.data = data;
        this.status = status;
    }

    /**
     * Returns the status true/false of the request's success
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Returns the data object property in the response object.
     */
    public Object getData() {
        return data;
    }

    /**
     * Serialises the response object.
     * @return a byte array of the serialised object.
     */
    public byte[] serialise() {
        // TODO - determine if this code is redundant.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }
}