package Exceptions;

import BillboardSupport.Billboard;

public class BillboardUnscheduledException extends Exception {
    Billboard billboard;
    public BillboardUnscheduledException(Billboard billboard) {
        super(String.format("Billboard \"%s\" was already not scheduled.", billboard.getBillboardName()));
        this.billboard = billboard;
    }
}
