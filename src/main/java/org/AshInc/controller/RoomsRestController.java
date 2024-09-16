package org.AshInc.controller;

import org.AshInc.model.Chatter;
import org.AshInc.model.Room;
import org.AshInc.service.ChatterService;
import org.AshInc.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomsRestController {

    @Autowired
    RoomService roomService;

    @Autowired
    ChatterService chatterService;

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
//        System.out.println(currRoom.getRoomName()+" "+currChatter.getLogin());
        boolean isValid = login.equals(currChatter.getLogin()) && roomName.equals(currRoom.getRoomName());
        return ResponseEntity.ok(isValid);
    }
}
