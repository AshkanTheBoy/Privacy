package org.AshInc.controller;

import org.AshInc.model.Chatter;
import org.AshInc.model.Room;
import org.AshInc.service.ChatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class WebController {

    @Autowired
    ChatterService chatterService;

    @GetMapping(value = "/main/{login}")
    public String getMain(@PathVariable("login") String login, HttpSession session, Model model){
        Chatter chatter = (Chatter) session.getAttribute("chatter");
        model.addAttribute("chatter", chatter);
        model.addAttribute("room", new Room());
        return "main";
    }

}
