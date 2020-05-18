package SocketCommunication;
import java.io.Serializable;


public class Response implements Serializable {
    public boolean isStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    // pass/fail
    private boolean status;
    // generic data object for passing data back to client
    private Object data;

    public Response(boolean status, Object data) {
        this.data = data;
        this.status = status;
    }
}