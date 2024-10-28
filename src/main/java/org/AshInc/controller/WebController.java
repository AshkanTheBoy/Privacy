package org.AshInc.controller; // Define the package for this controller

import org.AshInc.model.Chatter;
import org.AshInc.model.Room;
import org.AshInc.service.ChatterService;
import org.AshInc.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
        if (chatter == null) {
            return "redirect:/login"; // Redirect to the login page if chatter is not found
        }
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
    @GetMapping({"/login", "", "/"})
    public String loginUser(Model model, HttpSession session) {
        Chatter sessionChatter = (Chatter) session.getAttribute("chatter"); // Get the current chatter from session
        // System.out.println("SESSION CHATTER");
        // System.out.println(sessionChatter);
        if (sessionChatter != null) {
            return "redirect:/main/" + sessionChatter.getLogin(); // Redirect to the main page if already logged in
        }
        model.addAttribute("chatter", new Chatter()); // Add a new chatter object to the model
        return "login"; // Return the login view
    }

    // Endpoint to handle signup requests
    @GetMapping({"/signup"})
    public String registerUser(Model model, HttpSession session) {
        Chatter sessionChatter = (Chatter) session.getAttribute("chatter"); // Get the current chatter from session
        // System.out.println(sessionChatter);
        if (sessionChatter != null) {
            return "redirect:/main/" + sessionChatter.getLogin(); // Redirect to the main page if already logged in
        }
        model.addAttribute("chatter", new Chatter()); // Add a new chatter object to the model
        return "register"; // Return the registration view
    }

    // Endpoint to handle logout requests
//    @GetMapping("/logout")
//    public String logOut(HttpSession session) {
//        session.invalidate(); // Invalidate the session to log out the user
//        return "redirect:/login"; // Redirect to the login view
//    }
    @GetMapping("/logout")
    public String logOut(HttpSession session, HttpServletResponse response) {
        session.invalidate(); // Invalidate the session to log out the user

//        // Clear a specific cookie
//        Cookie cookie = new Cookie("JSESSIONID", null);
//        cookie.setPath("/"); // Set the path to match the cookie's path
//        cookie.setMaxAge(0); // Set the max age to 0 to delete it
//        response.addCookie(cookie); // Add the cookie to the response

        return "redirect:/login"; // Redirect to the login view
    }
}
