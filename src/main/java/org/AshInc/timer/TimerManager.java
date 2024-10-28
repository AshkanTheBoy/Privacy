package org.AshInc.timer;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

// Indicates that this class is a Spring component
@Component
public class TimerManager {

    // ConcurrentHashMap to store timers associated with room names
    @Getter // Generates a getter for the timers field
    private static final ConcurrentHashMap<String, Timer> timers = new ConcurrentHashMap<>();

    // Adds a new timer for a specified room name
    public static void addTimer(String roomName, Timer timer) {
        timers.put(roomName, timer); // Stores the timer in the map
    }

    // Retrieves the timer associated with the specified room name
    public static Timer getTimer(String roomName) {
        return timers.get(roomName); // Returns the timer, or null if not found
    }
}
