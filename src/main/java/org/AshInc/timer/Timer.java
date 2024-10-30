package org.AshInc.timer;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.AshInc.model.OutputMessage;
import org.AshInc.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component // Indicates that this class is a Spring component
@Data // Generates getters and setters for the class fields
@NoArgsConstructor // Generates a no-argument constructor
public class Timer {

    private String value; // The current timer value (in HH:MM:SS format)
    private String roomName; // The name of the room associated with this timer
    private SimpMessagingTemplate template; // For sending messages to WebSocket clients
    private boolean status = false; // Indicates whether the timer is running

    @Setter
    private static RoomService roomService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Creates a single-threaded scheduler
    private ScheduledFuture<?> scheduledFuture; // Represents the scheduled task

    // Starts the timer with a specified duration
    public void startTimer(String roomName, String duration) {
        this.roomName = roomName; // Sets the room name
        this.value = duration; // Sets the initial timer value
        status = true; // Indicates that the timer is active
        scheduleTimer(); // Schedules the timer task
    }

    // Schedules the timer to send updates at fixed intervals
    private void scheduleTimer() {
        // Cancels any existing scheduled task
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
        }
        // Schedules the task to run every second
        scheduledFuture = scheduler.scheduleAtFixedRate(this::sendTimerValue, 0, 1, TimeUnit.SECONDS);
    }

    // Sends the current timer value to the WebSocket topic
    private void sendTimerValue() {
        if (status) {
            // Splits the current timer value into hours, minutes, and seconds
            String[] digitsStr = this.value.split(":");
            int[] timerDigits = new int[3];
            for (int i = 0; i < digitsStr.length; i++) {
                timerDigits[i] = Integer.parseInt(digitsStr[i]); // Converts to integer
            }
            // Decrements the seconds
            timerDigits[2]--;
            if (timerDigits[2] < 0) {
                timerDigits[1]--; // Decrements minutes
                timerDigits[2] = 59; // Resets seconds
            }
            if (timerDigits[1] < 0) {
                timerDigits[0]--; // Decrements hours
                timerDigits[1] = 59; // Resets minutes
            }
            // Formats the timer value back to HH:MM:SS
            for (int i = 0; i < timerDigits.length; i++) {
                if (timerDigits[i] < 10) {
                    digitsStr[i] = String.format("%02d", timerDigits[i]); // Pads with zeros
                } else {
                    digitsStr[i] = String.valueOf(timerDigits[i]);
                }
            }
            this.value = String.format("%s:%s:%s", digitsStr[0], digitsStr[1], digitsStr[2]); // Updates the timer value
            // Sends the updated timer value to the WebSocket topic
            template.convertAndSend("/topic/" + roomName, new OutputMessage(null, this.value, null));
            // Checks if the timer has reached zero
            if (this.value.equals("00:00:00")) {
                template.convertAndSend("/topic/" + roomName, new OutputMessage("System", "clear", null)); // Sends a clear message
                stopTimer(); // Stops the timer
            }
        }
    }

    // Stops the timer
    public void stopTimer() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false); // Cancels the scheduled task
        }
        status = false; // Updates the status to inactive
        roomService.deleteRoomByName(roomName);
    }

    // Constructor for dependency injection
    @Autowired
    public Timer(SimpMessagingTemplate template) {
        this.template = template; // Initializes the template for sending messages
    }

}
