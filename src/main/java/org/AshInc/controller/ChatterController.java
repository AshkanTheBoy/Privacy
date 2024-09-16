package org.AshInc.controller;

import org.AshInc.model.Chatter;
import org.AshInc.model.Room;
import org.AshInc.service.ChatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
@Controller
public class ChatterController {
    @Autowired
    private ChatterService chatterService;

    @GetMapping(value = "/users")
    public List<Chatter> getAllChatters(){
        return chatterService.findAll();
    }

    @PostMapping(value = "/add")
    public String addNewChatter(@ModelAttribute("chatter") Chatter chatter, HttpSession session){
        System.out.println(chatter);
        session.setAttribute("chatter",chatter);
        String login = chatter.getLogin();
        String password = chatter.getPasswordHash();
        if ((!login.isBlank()||!login.isEmpty())&&(!password.isBlank()||!password.isEmpty())){
            if (chatterService.findUserByLogin(login)!=null){
                System.out.println("This user already exists");
            } else {
                chatterService.addNewUser(chatter);
                String redirect = String.format("redirect:/main");
                System.out.println(redirect);
                return "redirect:/main/"+chatter.getLogin();
            }
        }
        return "redirect:/";
    }

    @GetMapping({"/",""})
    public String registerUser(Model model){
        model.addAttribute("chatter", new Chatter());
        return "index";
    }
}
