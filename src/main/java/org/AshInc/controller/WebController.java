package org.AshInc.controller;

import org.AshInc.model.Chatter;
import org.AshInc.model.Room;
import org.AshInc.service.ChatterService;
import org.AshInc.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

@Controller
public class WebController {

    @Autowired
    ChatterService chatterService;

    @Autowired
    RoomService roomService;

    @GetMapping(value = "/main/{login}")
    public String getMain(@PathVariable("login") String login, HttpSession session, Model model){
        Chatter chatter = (Chatter) session.getAttribute("chatter");
        if (chatter==null){
            return "redirect:/login";
        }
        model.addAttribute("chatter", chatter);
        Room room = (Room) session.getAttribute("room");
        if (room!=null){
            model.addAttribute("room",room);
            model.addAttribute("roomName",room.getRoomName());
        } else {
            model.addAttribute("room", new Room());
        }
        return "main";
    }

    @GetMapping({"/login","","/"})
    public String loginUser(Model model, HttpSession session){
        Chatter sessionChatter = (Chatter)session.getAttribute("chatter");
//        System.out.println("SESSION CHATTER");
//        System.out.println(sessionChatter);
        if (sessionChatter!=null){
            return "redirect:/main/"+sessionChatter.getLogin();
        }
        model.addAttribute("chatter", new Chatter());
        return "login";
    }

    @GetMapping({"/signup"})
    public String registerUser(Model model, HttpSession session){
        Chatter sessionChatter = (Chatter)session.getAttribute("chatter");
//        System.out.println(sessionChatter);
        if (sessionChatter!=null){
            return "redirect:/main/"+sessionChatter.getLogin();
        }
        model.addAttribute("chatter", new Chatter());
        return "register";
    }

    @GetMapping("/logout")
    public String logOut(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }

}


