package BillboardSupport;

import java.sql.Timestamp;

/**
 * Class which defines an occurrence object.
 * This object represents when a billboard occurs within the schedule.
 */
public class Occurrence implements Comparable<Occurrence> {
    final public Timestamp start; // Start date
    final public Timestamp end; // End date
    final public String name; // Billboard name

    /**
     * Constructs an Occurrence object.
     * @param start The start date of the billboard
     * @param end The end date of the billboard
     * @param name The billboard name
     */
    Occurrence(Timestamp start, Timestamp end, String name) {
        this.start = start;
        this.end = end;
        this.name = name;
    }

    /**
     * Takes a timestamp and returns whether the occurrence includes it or not
     * @param timestamp The timestamp to check
     * @return Whether it's within the range of this occurrence
     */
    public boolean includes(Timestamp timestamp){
        return timestamp.after(start) && timestamp.before(end);
    }

    /**
     * Compare two occurrence objects to each other
     * @param occurrence
     * @return and integer value associated with the difference between the two Occurrence objects
     */
    public int compareTo(Occurrence occurrence) {
        return this.start.compareTo(occurrence.start);
    }
}
