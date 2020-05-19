package Exceptions;

public class InvalidTokenException extends Exception{
    byte[] token;
    InvalidTokenException(byte[] token){
        super(String.format("Token %s is invalid", new String(token)));
        this.token = token;
    }
}
