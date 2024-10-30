package org.AshInc.controller; // Define the package for this controller

import org.AshInc.model.Chatter; // Import the Chatter model
import org.AshInc.model.Room;
import org.AshInc.service.ChatterService; // Import the ChatterService for user management
import org.AshInc.service.RoomService;
import org.apache.commons.text.StringEscapeUtils; // Import for escaping HTML
import org.springframework.beans.factory.annotation.Autowired; // Import for dependency injection
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller; // Import to indicate this class is a controller
import org.springframework.web.bind.annotation.*; // Import for mapping HTTP requests to handler methods
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Import for redirecting attributes

import javax.servlet.http.HttpSession; // Import for session management
import java.util.regex.Matcher; // Import for regex matching
import java.util.regex.Pattern; // Import for regex pattern matching

// Annotation to indicate that this class is a Spring MVC controller
@Controller
public class ChatterController {
    @Autowired // Automatically inject ChatterService instance
    private ChatterService chatterService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private AuthenticationProvider authenticationProvider;

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
                    password = new BCryptPasswordEncoder().encode(password);
                    chatter.setPasswordHash(password); // Set validated password
                    chatterService.addNewUser(chatter); // Add new user to the service
                    return "redirect:/customLogin"; // Redirect to login page
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
    @PostMapping(value = "/login")
    public String searchChatter(@ModelAttribute("chatter") Chatter chatter, HttpSession session, RedirectAttributes redirectAttributes) {
        // Escape HTML to prevent injection attacks
        String login = StringEscapeUtils.escapeHtml4(chatter.getLogin());
        String password = StringEscapeUtils.escapeHtml4(chatter.getPasswordHash());
        // Check if login and password are not blank or empty
        if ((!login.isBlank() || !login.isEmpty()) && (!password.isBlank() || !password.isEmpty())) {
            Chatter pendingChatter = chatterService.findUserByLogin(login); // Find user by login
            String hashedPass = "";
            if (pendingChatter!=null){
                hashedPass = pendingChatter.getPasswordHash();
            }
            // Check if user exists and password matches
            if (new BCryptPasswordEncoder().matches(password,hashedPass)) {
                //Authenticate the user
                Authentication auth = authenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(login, password)
                );
                session.setAttribute("chatter", chatter); // Store chatter in session
                //If a user is connected to a room - retrieve it
                if (!chatter.getRooms().isEmpty()){
                    Room sessionRoom = chatter.getRooms().get(0);
                    Room jpaRoom = roomService.findByChatName(sessionRoom.getRoomName());
                    session.setAttribute("room",jpaRoom);
                }
                SecurityContextHolder.getContext().setAuthentication(auth);
                return "redirect:/main/" + login; // Redirect to main page with login
            } else {
                redirectAttributes.addFlashAttribute("error", "Incorrect login or password"); // Add error message
                return "redirect:/customLogin"; // Redirect to login page
            }
        }
        // Add error message for empty inputs
        redirectAttributes.addFlashAttribute("error", "Empty inputs are not allowed");
        return "redirect:/customLogin"; // Redirect to login page
    }
}
