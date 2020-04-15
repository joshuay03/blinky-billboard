package SocketCommunication;

/**
 * An interface containing methods related to communicating with other sockets.
 * A contract which states that the class implementing this must communicate
 * with another socket by both sending a message and recieving messages.
 */
public interface SocketCommunication {
    /**
     * A method to communicate with another socket and send a message.
     * @param msg a string containing the output being sent to another socket.
     */
    public void sendOutput(String msg);

    /**
     * // A method to communicate with another socket and retrieve a message.
     * @return a string containing the response from another socket.
     */
    public String retrieveInput();
}
