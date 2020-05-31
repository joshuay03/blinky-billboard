package Exceptions;

import BillboardSupport.Billboard;

/**
 * Exception used if the billboard could not be unscheduled.
 */
public class BillboardUnscheduledException extends Exception {
    String billboardName;
    public BillboardUnscheduledException(String billboardName) {
        super(String.format("Billboard \"%s\" was already not scheduled.", billboardName));
        this.billboardName = billboardName;
    }
}
