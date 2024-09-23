package org.AshInc.timer;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TimerManager {
    @Getter
    private static final ConcurrentHashMap<String, Timer> timers = new ConcurrentHashMap<>();

    public static void addTimer(String roomName, Timer timer){
        timers.put(roomName, timer);
    }

    public static Timer getTimer(String roomName){
        return timers.get(roomName);
    }
}
