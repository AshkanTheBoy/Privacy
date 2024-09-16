package org.AshInc.controller;

import org.AshInc.model.OutputMessage;
import org.AshInc.model.Room;
import org.AshInc.service.MessageService;
import org.AshInc.service.ChatterService;
import org.AshInc.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.Date;
import org.AshInc.model.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;

@Controller
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
}
