package BillboardSupport;

import java.sql.Timestamp;

public class Occurrence {
    final public Timestamp start;
    final public Timestamp end;
    final public String name;

    Occurrence(Timestamp start, Timestamp end, String name) {
        this.start = start;
        this.end = end;
        this.name = name;
    }

    public boolean includes(Timestamp timestamp){
        return timestamp.after(start) && timestamp.before(end);
    }
}
