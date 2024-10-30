package org.AshInc.controller; // Define the package for this controller

import org.AshInc.model.Chatter; // Import the Chatter model
import org.AshInc.model.Room; // Import the Room model
import org.AshInc.service.ChatterService; // Import the ChatterService for managing chatters
import org.AshInc.service.RoomService; // Import the RoomService for managing rooms
import org.springframework.beans.factory.annotation.Autowired; // Import for dependency injection
import org.springframework.stereotype.Controller; // Import for marking this class as a controller
import org.springframework.ui.Model; // Import for adding attributes to the model
import org.springframework.web.bind.annotation.GetMapping; // Import for handling GET requests
import org.springframework.web.bind.annotation.PathVariable; // Import for extracting path variables

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // Import for session management

// Annotation to indicate that this class is a Spring MVC controller
@Controller
public class WebController {

    @Autowired
    ChatterService chatterService; // Automatically inject ChatterService

    @Autowired
    RoomService roomService; // Automatically inject RoomService

    // Endpoint to get the main chat interface for a user
    @GetMapping(value = "/main/{login}")
    public String getMain(@PathVariable("login") String login, HttpSession session, Model model) {
        Chatter chatter = (Chatter) session.getAttribute("chatter"); // Get the current chatter from session
        model.addAttribute("chatter", chatter); // Add the chatter to the model
        Room room = (Room) session.getAttribute("room"); // Get the current room from session
        if (room != null) {
            model.addAttribute("room", room); // Add the room to the model
            model.addAttribute("roomName", room.getRoomName()); // Add the room name to the model
        } else {
            model.addAttribute("room", new Room()); // Add a new room object to the model if none exists
        }
        return "main"; // Return the main view
    }

    // Endpoint to handle login requests
    @GetMapping({"/customLogin", "", "/","login"})
    public String loginUser(Model model, HttpSession session) {
        Chatter sessionChatter = (Chatter) session.getAttribute("chatter"); // Get the current chatter from session
        if (sessionChatter != null) {
            return "redirect:/main/" + sessionChatter.getLogin(); // Redirect to the main page if already logged in
        }
        model.addAttribute("chatter", new Chatter()); // Add a new chatter object to the model
        return "customLogin"; // Return the login view
    }

    // Endpoint to handle signup requests
    @GetMapping({"/signup"})
    public String registerUser(Model model, HttpSession session) {
        Chatter sessionChatter = (Chatter) session.getAttribute("chatter"); // Get the current chatter from session
        if (sessionChatter != null) {
            return "redirect:/main/" + sessionChatter.getLogin(); // Redirect to the main page if already logged in
        }
        model.addAttribute("chatter", new Chatter()); // Add a new chatter object to the model
        return "register"; // Return the registration view
    }

    // Endpoint to handle logout requests
    @GetMapping("/logout")
    public String logOut(HttpSession session, HttpServletResponse response) {
        session.invalidate(); // Invalidate the session to log out the user
        return "redirect:/customLogin"; // Redirect to the login view
    }
}
