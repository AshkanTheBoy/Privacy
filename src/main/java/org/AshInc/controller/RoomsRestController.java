package org.AshInc.controller; // Define the package for this REST controller

import org.AshInc.model.Chatter;
import org.AshInc.model.Message;
import org.AshInc.model.Room;
import org.AshInc.service.ChatterService;
import org.AshInc.service.MessageService;
import org.AshInc.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Annotation to indicate that this class is a REST controller
@RestController
@RequestMapping("/api") // Base URL for all endpoints in this controller
public class RoomsRestController {

    @Autowired
    RoomService roomService; // Automatically inject RoomService

    @Autowired
    ChatterService chatterService; // Automatically inject ChatterService

    @Autowired
    MessageService messageService; // Automatically inject MessageService

    // Endpoint to get a list of all rooms
    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms(); // Return all rooms from the service
    }

    // Endpoint to check if a room name is available
    @GetMapping("/checkName/{chatName}")
    public boolean checkName(@PathVariable("chatName") String chatName) {
        return roomService.findByChatName(chatName) == null; // Return true if room name is available
    }

    // Endpoint to check the capacity of a room
    @GetMapping("/checkCapacity/{chatName}")
    public boolean checkCapacity(@PathVariable("chatName") String chatName) {
        Room room = roomService.findByChatName(chatName); // Find the room by name
//        if (room == null) {
//            return true; // If room doesn't exist, return true (considered available)
//        }
        return room.getSlots() < 2; // Return true if room capacity is less than 2
    }

    // Endpoint to verify the current user session and room name
    @GetMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestParam(name = "login") String login, @RequestParam(name = "roomName") String roomName, HttpSession session) {
        Room currRoom = (Room) session.getAttribute("room"); // Get current room from session
        System.out.printf("CURRENT ROOM: %s\nHTML ROOM: %s\n",currRoom.getRoomName(),roomName);
        Chatter currChatter = (Chatter) session.getAttribute("chatter"); // Get current chatter from session
        System.out.printf("CURRENT CHATTER: %s\nHTML CHATTER: %s\n",currChatter.getLogin(),login);
        boolean isValid = login.equals(currChatter.getLogin()) && roomName.equals(currRoom.getRoomName()); // Check validity
        System.out.println(isValid);

        if (isValid) {
            return ResponseEntity.ok().build(); // Return 200 OK if valid
        } else {
            return ResponseEntity.badRequest().build(); // Return 400 Bad Request if not valid
        }
    }

    // Endpoint to verify the current user's login
    @GetMapping("/verifyLogin")
    public ResponseEntity<Boolean> verifyLogin(@RequestParam(name = "login") String login, HttpSession session) {
        Chatter currChatter = (Chatter) session.getAttribute("chatter"); // Get current chatter from session
        boolean isValid = login.equals(currChatter.getLogin()); // Check if login matches

        if (isValid) {
            return ResponseEntity.ok().build(); // Return 200 OK if valid
        } else {
            return ResponseEntity.badRequest().build(); // Return 400 Bad Request if not valid
        }
    }

    // Endpoint to verify if a room exists
    @GetMapping("/verifyRoom")
    public ResponseEntity<Void> verifyRoom(@RequestParam("room") String roomName) {
        boolean roomExists = roomService.findByChatName(roomName) != null; // Check if room exists

        if (roomExists) {
            return ResponseEntity.ok().build(); // Return 200 OK if room exists
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if room does not exist
        }
    }

    // Endpoint to verify room name, time, and password fields
    @GetMapping("/verifyFields")
    public ResponseEntity<String> verifyFields(@RequestParam(name = "roomName") String roomName,
                                               @RequestParam(name = "time") String time,
                                               @RequestParam(name = "password") String password) {
        // Check if room name and time fields are not blank or empty
        boolean validFields = (!roomName.isBlank() && !roomName.isEmpty()) &&
                (!time.isEmpty() && !time.isBlank());

        if (validFields) {
            // Define regex patterns for validation
            Pattern roomPattern = Pattern.compile("^[a-zA-Z0-9_-]{3,32}$");
            Matcher roomMatcher = roomPattern.matcher(roomName);
//            Pattern timePattern = Pattern.compile("^[0-9]{3}:[0-9]{2}:[0-9]{2}$");
//            Matcher timeMatcher = timePattern.matcher(time);
            String[] durations = {"168:00:00","024:00:00","001:00:00","000:01:00","000:00:03"};
            boolean isTimeValid = false;
            for (String val: durations){
                if (time.equals(val)){
                    isTimeValid = true;
                }
            }
            Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9]{5,128}$");
            Matcher passwordMatcher = passwordPattern.matcher(password);

            // Check if all fields match their respective patterns
            if (roomMatcher.matches() && isTimeValid && passwordMatcher.matches()) {
                return ResponseEntity.ok("OK"); // Return 200 OK if all fields are valid
            } else {
                return ResponseEntity.badRequest().body("Not valid request fields"); // Return 400 Bad Request if invalid
            }
        } else {
            return ResponseEntity.badRequest().body("Not valid request fields"); // Return 400 Bad Request if fields are blank or empty
        }
    }

    // Endpoint to get the last messages from a specific room
    @GetMapping("/getMessages/{roomName}")
    public List<Message> getRoomMessages(@PathVariable("roomName") String roomName) {
        return messageService.getLastMessagesByRoomName(roomName); // Return last messages for the room
    }

    // Endpoint to check if a session exists for the current user
    @GetMapping("/checkForSession")
    public ResponseEntity<String> checkForSession(HttpSession session) {
        Chatter sessionChatter = (Chatter) session.getAttribute("chatter"); // Get current chatter from session

        if (sessionChatter != null) {
            Chatter chatter = chatterService.findUserByLogin(sessionChatter.getLogin()); // Find the chatter
            if (!chatter.getRooms().isEmpty()) {
                Room jpaRoom = roomService.findByChatName(chatter.getRooms().get(0).getRoomName());
                session.setAttribute("room",jpaRoom);
                return ResponseEntity.ok(chatter.getRooms().get(0).getRoomName()); // Return the first room name if available
            }
            return ResponseEntity.notFound().build(); // Return 404 if no rooms
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if no session exists
        }
    }

    // Endpoint to verify the password for a specific room
    @GetMapping("/verifyPassword/{roomName}/{password}")
    public ResponseEntity<Void> verifyPassword(@PathVariable("roomName") String roomName, @PathVariable("password") String password, HttpSession session) {
        Room room = roomService.findByChatName(roomName); // Find the room by name
        boolean roomExists = room != null; // Check if room exists
        boolean isPasswordCorrect = roomExists && room.getPassword().equals(password); // Check if the password matches

        Chatter sessionChatter = (Chatter) session.getAttribute("chatter"); // Get current chatter from session

        // Check if the chatter is already in the room
        if (sessionChatter.getRooms().contains(room)) {
            return ResponseEntity.ok().build(); // Return 200 OK if the chatter is already in the room
        }

        if (roomExists && isPasswordCorrect) {
            return ResponseEntity.ok().build(); // Return 200 OK if room exists and password is correct
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if room doesn't exist or password is incorrect
        }
    }
}
