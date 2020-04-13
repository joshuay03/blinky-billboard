package IConnectable;

/**
 * An interface describing methods for basic communication between two sockets.
 * @author Nick
 * @version 0
 */
public interface Connectible {
    public void sendOutput();
    public void retrieveInput();
    public void closeConnection();
}
