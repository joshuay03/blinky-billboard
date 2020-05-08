package Exceptions;

public class UserAlreadyExistsException extends Exception{
    public UserAlreadyExistsException(String username){
        super(String.format("User '%s' already exists in database", username));
    }
}
