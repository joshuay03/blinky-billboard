package BillboardSupport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class for instantiating a schedule for a given billboard.
 * Associates a start time, duration, interval, and a date with a billboard.
 */
public class Schedule implements Serializable, Comparable<Schedule>, Iterable<Occurrence> {
    public Timestamp StartTime;
    public int duration; // Minutes
    public int repeatInterval; // Minutes
    public String billboardName;
    public Timestamp scheduledAt;

    /**
     * Instantiates a schedule object.
     * @param startTime The time the billboard should display at
     * @param duration The duration of the billboard
     * @param repeatInterval How often the billboard should display
     * @param billboardName The name of the billboard
     */
    public Schedule(Timestamp startTime, int duration, int repeatInterval, String billboardName) {
        this(startTime, duration, repeatInterval, billboardName, Timestamp.valueOf(LocalDateTime.now()));
    }

    /**
     * Instantiates a schedule object with a schedule date
     * @param startTime The time the billboard should display at
     * @param duration The duration of the billboard
     * @param repeatInterval How often the billboard should display
     * @param billboardName The name of the billboard
     * @param scheduledAt The date the billboard should be displayed
     */
    public Schedule(Timestamp startTime, int duration, int repeatInterval, String billboardName, Timestamp scheduledAt) {
        this.StartTime = startTime;
        this.duration = duration;
        this.repeatInterval = repeatInterval;
        this.billboardName = billboardName;
        this.scheduledAt = scheduledAt;
    }

    /**
     * Takes a schedule and extrapolates all of its occurrences up until the given timestamp
     * @param until Last occurrence to be retrieved
     * @return An array of all of the billboard's occurrences
     */
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

    /**
     * Checks whether a given schedule would result in showing the given timestamp
     * @param timestamp The timestamp to check for
     * @return True if the schedule would show the timestamp, false if not
     */
    public boolean includes(Timestamp timestamp) {
        if (timestamp.before(StartTime)) return false;
        long timestampMins = timestamp.getTime() / 60000;
        long startTimeMins = this.StartTime.getTime() / 60000;
        long minutesSinceStartOfLastOccurrence = (timestampMins % repeatInterval) - (startTimeMins % repeatInterval);
        return minutesSinceStartOfLastOccurrence <= duration; // If it's been less than or as many {duration} minutes since the time of the last
        // occurrence, the schedule's billboard should show
    }

    /**
     * Comparing schedules will result in a comparison of their creation times
     * @param schedule compare a schedule to a schedule
     */
    @Override
    public int compareTo(Schedule schedule) {
        return this.scheduledAt.compareTo(schedule.scheduledAt);
    }

    @Override
    public Iterator<Occurrence> iterator() {
        return new Iterator<>() {
            Timestamp currTime;
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Occurrence next() {
                Timestamp Start = Timestamp.valueOf(currTime.toLocalDateTime());
                Timestamp AfterDuration = Timestamp.valueOf(currTime.toLocalDateTime().plusMinutes(duration));
                currTime = Timestamp.valueOf(currTime.toLocalDateTime().plusMinutes(repeatInterval));
                return new Occurrence(Start, AfterDuration, billboardName);
            }
        };
    }
}