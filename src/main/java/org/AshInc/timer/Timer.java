package org.AshInc.timer;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.AshInc.controller.RoomController;
import org.AshInc.model.Chatter;
import org.AshInc.model.Message;
import org.AshInc.model.OutputMessage;
import org.AshInc.model.Room;
import org.AshInc.repository.MessageRepository;
import org.AshInc.repository.RoomRepository;
import org.AshInc.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Data
@NoArgsConstructor
public class Timer {

    private String value;
    private String roomName;
    private SimpMessagingTemplate template;
    private boolean status = false;


    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;


    public void startTimer(String roomName, String duration){
        this.roomName = roomName;
        this.value = duration;
        status = true;
        scheduleTimer();
    }

    private void scheduleTimer() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
        }

        scheduledFuture = scheduler.scheduleAtFixedRate(this::sendTimerValue, 0, 1, TimeUnit.SECONDS);
    }

    private void sendTimerValue() {
        if (status) {
            String[] digitsStr = this.value.split(":");
            int[] timerDigits = new int[3];
            for (int i=0; i<digitsStr.length; i++){
                timerDigits[i] = Integer.parseInt(digitsStr[i]);
            }
            timerDigits[2]--;
            if (timerDigits[2]<0){
                timerDigits[1]--;
                timerDigits[2]=59;
            }
            if (timerDigits[1]<0){
                timerDigits[0]--;
                timerDigits[1]=59;
            }
            for (int i=0; i< timerDigits.length; i++){
                if (timerDigits[i]<10){
                    digitsStr[i] = String.format("%02d",timerDigits[i]);
                } else {
                    digitsStr[i] = String.valueOf(timerDigits[i]);
                }
            }
            this.value = String.format("%s:%s:%s",digitsStr[0],digitsStr[1],digitsStr[2]);
            template.convertAndSend("/topic/" + roomName, new OutputMessage(null, this.value, null));
            if (this.value.equals("00:00:00")) {
                template.convertAndSend("/topic/" + roomName, new OutputMessage("System", "clear", null));
                stopTimer();
            }
        }
    }

    public void stopTimer() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        status = false;
    }

    @Autowired
    public Timer(SimpMessagingTemplate template){
        this.template = template;
    }

}
