package org.AshInc.controller; // Define the package for this controller

import org.AshInc.model.Chatter;
import org.AshInc.model.Room;
import org.AshInc.service.ChatterService;
import org.AshInc.service.RoomService;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Annotation to indicate that this class is a Spring MVC controller
@Controller
public class ChatterController {
    @Autowired // Automatically inject ChatterService instance
    private ChatterService chatterService;

    @Autowired
    private RoomService roomService;

    // Method to handle adding a new chatter
    @PostMapping(value = "/add")
    public String addNewChatter(@ModelAttribute("chatter") Chatter chatter, RedirectAttributes redirectAttributes) {
        // Escape HTML to prevent injection attacks
        String login = StringEscapeUtils.escapeHtml4(chatter.getLogin());
        String password = StringEscapeUtils.escapeHtml4(chatter.getPasswordHash());

        // Check if login and password are not blank or empty
        if ((!login.isBlank() || !login.isEmpty()) && (!password.isBlank() || !password.isEmpty())) {
            // Check if the user already exists
            if (chatterService.findUserByLogin(login) != null) {
                redirectAttributes.addFlashAttribute("error", "This user already exists"); // Add error message
                return "redirect:/signup"; // Redirect to signup page
            } else {
                // Define regex patterns for validating login and password
                Pattern loginPattern = Pattern.compile("^[a-zA-Z0-9_-]{3,32}$");
                Matcher loginMatcher = loginPattern.matcher(login);
                Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9]{5,128}$");
                Matcher passwordMatcher = passwordPattern.matcher(password);

                // Check if login and password match the defined patterns
                if (loginMatcher.matches() && passwordMatcher.matches()) {
                    chatter.setLogin(login); // Set validated login
                    chatter.setPasswordHash(password); // Set validated password
                    chatterService.addNewUser(chatter); // Add new user to the service
                    return "redirect:/login"; // Redirect to login page
                } else {
                    // Add error message for invalid username or password
                    redirectAttributes.addFlashAttribute("error", "Invalid username or password\nFor login: 3-16 characters, dashes and underscores are allowed\nFor password: 5-128 characters or numbers");
                    return "redirect:/signup"; // Redirect to signup page
                }
            }
        }
        // Add error message for empty inputs
        redirectAttributes.addFlashAttribute("error", "Empty inputs are not allowed");
        return "redirect:/signup"; // Redirect to signup page
    }

    // Method to handle searching for a chatter
    @GetMapping(value = "/get")
    public String searchChatter(@ModelAttribute("chatter") Chatter chatter, HttpSession session, RedirectAttributes redirectAttributes) {
        // Escape HTML to prevent injection attacks
        String login = StringEscapeUtils.escapeHtml4(chatter.getLogin());
        String password = StringEscapeUtils.escapeHtml4(chatter.getPasswordHash());

        // Check if login and password are not blank or empty
        if ((!login.isBlank() || !login.isEmpty()) && (!password.isBlank() || !password.isEmpty())) {
            Chatter pendingChatter = chatterService.findUserByLogin(login); // Find user by login
            // Check if user exists and password matches
            if (pendingChatter != null && password.equals(pendingChatter.getPasswordHash())) {
                session.setAttribute("chatter", chatter); // Store chatter in session
                if (!chatter.getRooms().isEmpty()){
                    Room sessionRoom = chatter.getRooms().get(0);
                    Room jpaRoom = roomService.findByChatName(sessionRoom.getRoomName());
                    session.setAttribute("room",jpaRoom);
                }
                return "redirect:/main/" + login; // Redirect to main page with login
            } else {
                redirectAttributes.addFlashAttribute("error", "Incorrect login or password"); // Add error message
                return "redirect:/login"; // Redirect to login page
            }
        }
        // Add error message for empty inputs
        redirectAttributes.addFlashAttribute("error", "Empty inputs are not allowed");
        return "redirect:/login"; // Redirect to login page
    }
}
