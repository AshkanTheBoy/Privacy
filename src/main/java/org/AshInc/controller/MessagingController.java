package org.AshInc.controller;

import org.AshInc.model.OutputMessage;
import org.AshInc.model.Room;
import org.AshInc.service.MessageService;
import org.AshInc.service.ChatterService;
import org.AshInc.service.RoomService;
import org.AshInc.timer.Timer;
import org.AshInc.timer.TimerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import java.util.Date;
import org.AshInc.model.Message;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class MessagingController {

    private final SimpMessagingTemplate template;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RoomService roomService;

    public MessagingController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @PostMapping(value = "/saveMessage")
    public String saveMessage(@RequestBody Message message){
        String dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        message.setSendingTime(dateTime);
        Room room = roomService.findByChatName(message.getRoomName());
        room.getMessages().add(message);
        message.setRoom(room);
        System.out.println(room);
        System.out.println(message);
        messageService.saveMessage(message);
        return "redirect:/main/"+message.getChatterLogin();
    }

    @MessageMapping("/chat/{chatId}")
    public void send(@DestinationVariable("chatId") String chatName,
                     Message message){
        Date messageSent = new Date();
        String time = new SimpleDateFormat("HH:mm:ss").format(messageSent);
        template.convertAndSend("/topic/"+chatName,
                new OutputMessage(message.getChatterLogin(), message.getText(),time));
    }

    @GetMapping("/startTimer/{duration}/{roomName}")
    public void startTimer(@PathVariable("duration") String duration, @PathVariable("roomName") String roomName){
        Pattern pattern = Pattern.compile("^[0-9]{3}:[0-9]{2}:[0-9]{2}$");
        Matcher matcher = pattern.matcher(duration);
        if (matcher.matches()){
            Room room = roomService.findByChatName(roomName);
            Timer newTimer = new Timer(template);
            room.setTimer(newTimer);
            room.getTimer().startTimer(roomName,duration);
            TimerManager.addTimer(roomName, newTimer);
        } else {
            System.out.println("Wrong timer value");
        }
    }

    @GetMapping("/stopTimer/{roomName}")
    public void stopTimer(@PathVariable("roomName") String roomName) {
        TimerManager.getTimer(roomName).stopTimer();
    }

}
