package BillboardSupport;

import java.sql.Timestamp;

public class Occurrence implements Comparable<Occurrence> {
    final public Timestamp start;
    final public Timestamp end;
    final public String name;

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

    public int compareTo(Occurrence occurrence) {
        return this.start.compareTo(occurrence.start);
    }
}
