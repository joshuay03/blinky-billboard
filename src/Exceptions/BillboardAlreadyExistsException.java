package Exceptions;

import BillboardSupport.Billboard;

/**
 * Exception used if the billboard already exists within the database.
 */
public class BillboardAlreadyExistsException extends Exception{
    Billboard billboard;
    public BillboardAlreadyExistsException(Billboard billboard){
        super(String.format("Billboard with name \"%s\" already exists.", billboard.getBillboardName()));
        this.billboard = billboard;
    }

    public Billboard getBillboard() {
        return billboard;
    }
}
