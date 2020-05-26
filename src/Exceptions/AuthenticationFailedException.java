package Exceptions;

public class AuthenticationFailedException extends Exception{
    public AuthenticationFailedException(String username){
        super(String.format("Authentication failed for user '{%s}'", username));
    }
}
