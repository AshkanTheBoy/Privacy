package org.AshInc.controller; // Define the package for this controller

import org.AshInc.model.Message;
import org.AshInc.model.OutputMessage;
import org.AshInc.model.Room;
import org.AshInc.service.MessageService;
import org.AshInc.service.RoomService;
import org.AshInc.timer.Timer;
import org.AshInc.timer.TimerManager;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

// Annotation to indicate that this class is a REST controller
@RestController
public class MessagingController {

    private final SimpMessagingTemplate template; // Template for sending messages
    @Autowired // Automatically inject MessageService instance
    private MessageService messageService; // Service for managing messages
    @Autowired // Automatically inject RoomService instance
    private RoomService roomService; // Service for managing rooms

    @Autowired // Automatically inject RoomController instance
    private RoomController roomController; // Controller for room management

    // Constructor to initialize the messaging template
    public MessagingController(SimpMessagingTemplate template) {
        this.template = template;
    }

    // Method to handle saving a message
    @PostMapping(value = "/saveMessage")
    public String saveMessage(@RequestBody Message message) {
        // Format the current date and time
        String dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        message.setSendingTime(dateTime); // Set the sending time for the message

        // Find the room associated with the message
        Room room = roomService.findByChatName(message.getRoomName());
        room.getMessages().add(message); // Add the message to the room's message list
        message.setRoom(room); // Associate the message with the room
        message.setText(StringEscapeUtils.escapeHtml4(message.getText())); // Escape HTML in the message text
        messageService.saveMessage(message); // Save the message using the message service
        return "redirect:/main/" + message.getChatterLogin(); // Redirect to the main page for the user
    }

    // Method to handle sending messages via STOMP
    @MessageMapping("/chat/{chatId}")
    public void send(@DestinationVariable("chatId") String chatName, Message message) {
        Date messageSent = new Date(); // Get the current date and time
        String time = new SimpleDateFormat("HH:mm").format(messageSent); // Format the time

        // Send the message to the specified chat topic
        template.convertAndSend("/topic/" + chatName,
                new OutputMessage(message.getChatterLogin(), message.getText(), time));
    }

    // Method to start a timer for a room
    @GetMapping("/startTimer/{duration}/{roomName}")
    public void startTimer(@PathVariable("duration") String duration, @PathVariable("roomName") String roomName) {
        // Define patterns for validating timer duration
        boolean notFound = true;
        String[] durations = {"168:00:00","024:00:00","001:00:00","000:01:00","000:00:03"};
        for (String val: durations){
            // If the duration matches the expected format
            if (duration.equals(val)){
                Timer.setRoomService(roomService);
                Room room = roomService.findByChatName(roomName); // Find the room by name
                Timer newTimer = new Timer(template); // Create a new timer
                room.setTimer(newTimer); // Associate the timer with the room
                room.getTimer().startTimer(roomName, duration); // Start the timer with the specified duration
                TimerManager.addTimer(roomName, newTimer); // Add the timer to the TimerManager
                notFound = false;
                break;
            }
        }
        if (notFound){
            System.out.println("Wrong timer value"); // Log if the timer value is incorrect
        }
    }

    // Method to stop the timer for a room
    @GetMapping("/stopTimer/{roomName}")
    public void stopTimer(@PathVariable("roomName") String roomName) {
        TimerManager.getTimer(roomName).stopTimer(); // Stop the timer associated with the room
    }
}
