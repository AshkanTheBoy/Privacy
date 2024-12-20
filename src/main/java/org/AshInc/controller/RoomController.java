package org.AshInc.controller; // Define the package for this controller

import org.AshInc.model.Chatter; // Import the Chatter model
import org.AshInc.model.Room; // Import the Room model
import org.AshInc.service.ChatterService; // Import the ChatterService for managing chatters
import org.AshInc.service.MessageService; // Import the MessageService for message handling
import org.AshInc.service.RoomService; // Import the RoomService for managing rooms
import org.springframework.beans.factory.annotation.Autowired; // Import for dependency injection
import org.springframework.http.HttpStatus; // Import for HTTP status codes
import org.springframework.http.ResponseEntity; // Import for building response entities
import org.springframework.stereotype.Controller; // Import for defining a controller
import org.springframework.ui.Model; // Import for passing data to views
import org.springframework.web.bind.annotation.*; // Import for mapping HTTP requests
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Import for flash attributes

import javax.servlet.http.HttpSession; // Import for session management
import java.util.List; // Import for using List collections

// Annotation to indicate that this class is a Spring MVC controller
@Controller
@RequestMapping("/room")
public class RoomController {
    @Autowired
    RoomService roomService; // Automatically inject RoomService

    @Autowired
    ChatterService chatterService; // Automatically inject ChatterService

    @Autowired
    MessageService messageService; // Automatically inject MessageService

    // Endpoint to get a list of all rooms
    @GetMapping(value="/rooms")
    public List<Room> getAllRooms(){
        return roomService.getAllRooms(); // Return all rooms from the service
    }

    // Endpoint to connect a user to a room
    @PostMapping(value="/main/{login}")
    public String connectToRoom(@ModelAttribute("room") Room room,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        Chatter sessionChatter = (Chatter) session.getAttribute("chatter"); // Get the current chatter from session
        Chatter chatter = chatterService.findUserByLogin(sessionChatter.getLogin()); // Retrieve chatter from service
        Room existingRoom = roomService.findByChatName(room.getRoomName()); // Check if the room already exists

        if (existingRoom != null) {
            // Double-check the room for valid name
            boolean validName = (!existingRoom.getRoomName().isEmpty()) &&
                                (!existingRoom.getRoomName().isBlank());
            // Check if the room has available slots
            if (existingRoom.getSlots() < 2 && validName) {
                // Add the chatter to the room
                chatter.getRooms().add(existingRoom);
                existingRoom.getChatters().add(chatter);

                // Update session attributes
                session.setAttribute("room", existingRoom);
                session.setAttribute("roomName", existingRoom.getRoomName());
                session.setAttribute("isConnected", true);

                // Pass data to the model
                model.addAttribute("roomName", existingRoom.getRoomName());
                redirectAttributes.addFlashAttribute("roomName", existingRoom.getRoomName());
                model.addAttribute("chatter", chatter);

                roomService.incrementTakenSlots(existingRoom); // Increment taken slots in the room
                roomService.addNewRoom(existingRoom); // Update the room in the service
                model.addAttribute("messages", messageService.getLastMessagesByRoomName(existingRoom.getRoomName())); // Get last messages
            }
            return "redirect:/main/" + chatter.getLogin(); // Redirect to the main page for the user
        }
        return "redirect:/main/" + chatter.getLogin(); // Redirect if room does not exist
    }

    // Endpoint to create a new room
    @PostMapping(value = "/addRoom")
    public String createNewRoom(@ModelAttribute("room") Room room, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Chatter sessionChatter = (Chatter) session.getAttribute("chatter"); // Get the current chatter from session
        Chatter chatter = chatterService.findUserByLogin(sessionChatter.getLogin()); // Retrieve chatter from service
        Room existingRoom = roomService.findByChatName(room.getRoomName()); // Check if the room already exists

        // If the room does not exist
        if (existingRoom == null) {
            boolean validName = (!room.getRoomName().isEmpty()) && (!room.getRoomName().isBlank()); // Validate room name
            if (validName) {
                // Add the chatter to the new room
                chatter.getRooms().add(room);
                room.getChatters().add(chatter);

                // Update session attributes
                session.setAttribute("room", room);
                session.setAttribute("roomName", room.getRoomName());
                session.setAttribute("isConnected", true);

                // Pass data to the model
                model.addAttribute("roomName", room.getRoomName());
                redirectAttributes.addFlashAttribute("roomName", room.getRoomName());
                model.addAttribute("chatter", chatter);

                roomService.incrementTakenSlots(room); // Increment taken slots in the room
                roomService.addNewRoom(room); // Add the new room in the service
            }
            return "redirect:/main/" + chatter.getLogin(); // Redirect to the main page for the user
        }
        return "redirect:/main/" + chatter.getLogin(); // Redirect if room creation fails
    }

    // Endpoint to delete a room by name
    @DeleteMapping(value = "/delete/{roomName}")
    public ResponseEntity<Void> deleteRoomByName(@PathVariable("roomName") String roomName) {
        Room room = roomService.findByChatName(roomName); // Find the room by name
        if (room != null) {
            roomService.deleteRoomByName(roomName); // Delete the room
            System.out.println("CONTROLLER DELETE"); // Log the deletion for debugging
            return ResponseEntity.status(HttpStatus.OK).build(); // Return 200 OK
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if the room is not found
        }
    }
}
