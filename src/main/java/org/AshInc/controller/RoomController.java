package org.AshInc.controller;

import org.AshInc.model.Chatter;
import org.AshInc.model.Message;
import org.AshInc.model.Room;
import org.AshInc.service.ChatterService;
import org.AshInc.service.MessageService;
import org.AshInc.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class RoomController {
    @Autowired
    RoomService roomService;

    @Autowired
    ChatterService chatterService;

    @Autowired
    MessageService messageService;

    @GetMapping(value="/rooms")
    public List<Room> getAllRooms(){
        return roomService.getAllRooms();
    }

    @GetMapping(value = "/decrementRoomSlots/{roomName}")
    public ResponseEntity<Void> getRoomByName(@PathVariable("roomName")String roomName){
        Room room = roomService.findByChatName(roomName);
        if (room==null){
            return ResponseEntity.notFound().build();
        }
        roomService.decrementTakenSlots(room);
        roomService.updateRoom(room);
        System.out.println("WTF");
        System.out.println(room);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value="/main/{login}")
    public String connectToRoom(@ModelAttribute("room") Room room, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Chatter sessionChatter = (Chatter) session.getAttribute("chatter");
        Chatter chatter = chatterService.findUserByLogin(sessionChatter.getLogin());
        Room existingROom = roomService.findByChatName(room.getRoomName());
        if (existingROom != null) {
            boolean validName = (!existingROom.getRoomName().isEmpty())&&(!existingROom.getRoomName().isBlank());
            if (existingROom.getSlots() < 2&&validName) {
                chatter.getRooms().add(existingROom);
                existingROom.getChatters().add(chatter);
                session.setAttribute("room", existingROom);
                model.addAttribute("roomName",existingROom.getRoomName());
                redirectAttributes.addFlashAttribute("roomName", existingROom.getRoomName());
                model.addAttribute("chatter",chatter);
                roomService.incrementTakenSlots(existingROom);
                roomService.addNewRoom(existingROom);
            }
            return "redirect:/main/"+chatter.getLogin();
        }
        return "redirect:/main/"+chatter.getLogin();
    }

    @PostMapping(value = "/addRoom")
    public String createNewRoom(@ModelAttribute("room") Room room, HttpSession session, Model model, RedirectAttributes redirectAttributes){
        Chatter sessionChatter = (Chatter) session.getAttribute("chatter");
        System.out.println(sessionChatter);
        Chatter chatter = chatterService.findUserByLogin(sessionChatter.getLogin());
        System.out.println(chatter);
        Room existingROom = roomService.findByChatName(room.getRoomName());
        if (existingROom==null){
            boolean validName = (!room.getRoomName().isEmpty())&&(!room.getRoomName().isBlank());
            if (validName){
                chatter.getRooms().add(room);
                room.getChatters().add(chatter);
                session.setAttribute("room", room);
                model.addAttribute("roomName", room.getRoomName());
                redirectAttributes.addFlashAttribute("roomName",room.getRoomName());
                model.addAttribute("chatter", chatter);
                roomService.incrementTakenSlots(room);
                roomService.addNewRoom(room);
            }
            return "redirect:/main/"+chatter.getLogin();
        }
        return "redirect:/main/"+chatter.getLogin();
    }

    @DeleteMapping(value = "/delete/{roomName}")
    public ResponseEntity<Void> deleteRoomByName(@PathVariable("roomName") String roomName, HttpSession session, Model model){
        Chatter sessionChatter = (Chatter) session.getAttribute("chatter");
        Room room = roomService.findByChatName(roomName);
        for (Chatter chatter: room.getChatters()){
            System.out.println(chatter.getLogin());
            chatter.getRooms().remove(room);
        }
        for (Message message: room.getMessages()){
            messageService.remove(message);
        }
        roomService.deleteRoomByName(roomName);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
