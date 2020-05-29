package BillboardSupport;

import java.sql.Timestamp;

public class Schedule {
    public Timestamp StartTime;
    public int duration;
    public int repeatInterval;
    public int billboardID;

    public Schedule(Timestamp startTime, int duration, int repeatInterval, int billboardID) {
        StartTime = startTime;
        this.duration = duration;
        this.repeatInterval = repeatInterval;
        this.billboardID = billboardID;
    }
}
