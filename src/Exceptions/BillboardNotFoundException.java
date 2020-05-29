package Exceptions;

public class BillboardNotFoundException extends Exception{
    final public int id;
    public BillboardNotFoundException(int id){
        super(String.format("Billboard with id %d not found", id));
        this.id = id;
    }
}
