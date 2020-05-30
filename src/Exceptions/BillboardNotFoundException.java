package Exceptions;

public class BillboardNotFoundException extends Exception{
    final public String name;
    public BillboardNotFoundException(String name){
        super(String.format("Billboard with name \"%s\" not found", name));
        this.name = name;
    }
}
