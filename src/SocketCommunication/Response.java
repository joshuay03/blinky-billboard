package SocketCommunication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class Response implements Serializable {
    // pass/fail
    private boolean status;
    // generic data object for passing data back to client
    private Object data;

    public Response(boolean status, Object data) {
        this.data = data;
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public byte[] serialise() {
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