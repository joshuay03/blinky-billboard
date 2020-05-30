package BillboardSupport;

import java.sql.Timestamp;

public class Schedule {
    public Timestamp StartTime;
    public int duration; // Minutes
    public int repeatInterval;
    public String billboardName;
    public Timestamp scheduledAt;

    public Schedule(Timestamp startTime, int duration, int repeatInterval, String billboardName, Timestamp scheduledAt) {
        this.StartTime = startTime;
        this.duration = duration;
        this.repeatInterval = repeatInterval;
        this.billboardName = billboardName;
        this.scheduledAt = scheduledAt;
    }
}
