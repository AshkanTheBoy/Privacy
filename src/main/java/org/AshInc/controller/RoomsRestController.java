package org.AshInc.controller;

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

@RestController
@RequestMapping("/api")
public class RoomsRestController {

    @Autowired
    RoomService roomService;

    @Autowired
    ChatterService chatterService;

    @Autowired
    MessageService messageService;

    @GetMapping("/rooms")
    public List<Room> getAllRooms(){
        return roomService.getAllRooms();
    }

    @GetMapping("/checkName/{chatName}")
    public boolean checkName(@PathVariable("chatName") String chatName){
        return roomService.findByChatName(chatName)==null;
    }

    @GetMapping("/checkCapacity/{chatName}")
    public boolean checkCapacity(@PathVariable("chatName") String chatName){
        Room room = roomService.findByChatName(chatName);
        if (room==null){
            return true;
        }
        System.out.println(room.getSlots()<2);
        return room.getSlots()<2;
    }

    @GetMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestParam(name = "login") String login, @RequestParam(name = "roomName") String roomName, HttpSession session){
        Room currRoom = (Room) session.getAttribute("room");
        Chatter currChatter = (Chatter) session.getAttribute("chatter");
        boolean isValid = login.equals(currChatter.getLogin()) && roomName.equals(currRoom.getRoomName());
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/verifyLogin")
    public ResponseEntity<Boolean> verifyLogin(@RequestParam(name = "login") String login, HttpSession session){
        Chatter currChatter = (Chatter) session.getAttribute("chatter");
        boolean isValid = login.equals(currChatter.getLogin());
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/verifyRoom")
    public ResponseEntity<Void> verifyRoom(@RequestParam("room") String roomName){
        boolean roomExists = roomService.findByChatName(roomName)!=null;
        if (roomExists){
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/verifyFields")
    public ResponseEntity<String> verifyFields(@RequestParam(name = "roomName") String roomName,
                                                @RequestParam(name = "time") String time,
                                                 @RequestParam(name="password")String password){
        boolean validFields = (!roomName.isBlank()&&!roomName.isEmpty()) &&
                              (!time.isEmpty()&&!time.isBlank());
        if (validFields){
            Pattern roomPattern = Pattern.compile("^[a-zA-Z0-9_-]{3,32}$");
            Matcher roomMatcher = roomPattern.matcher(roomName);
            Pattern timePattern = Pattern.compile("^[0-9]{3}:[0-9]{2}:[0-9]{2}$");
            Matcher timeMatcher = timePattern.matcher(time);
            Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9]{5,128}$");
            Matcher passwordMatcher = passwordPattern.matcher(password);
            if (roomMatcher.matches()&&timeMatcher.matches()&&passwordMatcher.matches()){
                return ResponseEntity.ok("OK");
            } else {
                return ResponseEntity.badRequest().body("Not valid request fields");
            }
        } else {
            return ResponseEntity.badRequest().body("Not valid request fields");
        }
    }

    @GetMapping("/getMessages/{roomName}")
    public List<Message> getRoomMessages(@PathVariable("roomName")String roomName){
        List<Message> ms = messageService.getLastMessagesByRoomName(roomName);
        System.out.println("CONTROLLER=>");
        System.out.println(ms);
        return ms;
    }

    @GetMapping("/checkForSession")
    public ResponseEntity<String> checkForSession(HttpSession session){
        Chatter sessionChatter = (Chatter)session.getAttribute("chatter");
        //(boolean)session.getAttribute("isConnected")
//        Room room = roomService.findByChatName(roomName);
        if (sessionChatter!=null){
            Chatter chatter = chatterService.findUserByLogin(sessionChatter.getLogin());
            if (!chatter.getRooms().isEmpty()){
                return ResponseEntity.ok(chatter.getRooms().get(0).getRoomName());
            }
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @GetMapping("/getCurrentSubscription")
//    public ResponseEntity<String> getRoomName(HttpSession session){
//
//    }

    @GetMapping("/verifyPassword/{roomName}/{password}")
    public ResponseEntity<Void> verifyPassword(@PathVariable("roomName") String roomName, @PathVariable("password") String password, HttpSession session){
        Room room = roomService.findByChatName(roomName);
        boolean roomExists = room!=null;
        boolean isPasswordCorrect = room.getPassword().equals(password);
        Chatter sessionChatter = (Chatter)session.getAttribute("chatter");
        if (sessionChatter.getRooms().contains(room)){
            return ResponseEntity.ok().build();
        }
        if (roomExists&&isPasswordCorrect){
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
