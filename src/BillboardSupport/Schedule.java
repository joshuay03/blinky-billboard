package BillboardSupport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Schedule implements Serializable, Comparable<Schedule> {
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

    public Occurrence[] extrapolate(Timestamp until) {
        if (repeatInterval < duration) return new Occurrence[0];
        List<Occurrence> Occurrences = new ArrayList<>();
        Timestamp currTime = StartTime;
        Timestamp AfterDuration;
        while (currTime.before(until)) {
            AfterDuration = Timestamp.valueOf(currTime.toLocalDateTime().plusMinutes(duration));
            Occurrences.add(new Occurrence((Timestamp) currTime.clone(), AfterDuration, billboardName));
            long nextOccurrence = currTime.getTime() + ((repeatInterval * 60) * 1000);
            currTime.setTime(nextOccurrence);
        }
        return Occurrences.toArray(new Occurrence[0]);
    }

    public boolean includes(Timestamp timestamp) {
        if (timestamp.before(StartTime)) return false;
        long timestampMins = timestamp.getTime() / 60000;
        long minutesSinceStartOfLastOccurrence = ((timestamp.getTime() / 60000) % repeatInterval) - ((this.StartTime.getTime()/60000) % repeatInterval);
        return minutesSinceStartOfLastOccurrence <= duration;
    }

    @Override
    public int compareTo(Schedule schedule) {
        return this.scheduledAt.compareTo(schedule.scheduledAt);
    }
}