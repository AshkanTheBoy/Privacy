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
    public String addNewChatter(@ModelAttribute("chatter") Chatter chatter){
//        session.setAttribute("chatter",chatter);
        String login = chatter.getLogin();
        String password = chatter.getPasswordHash();
        if ((!login.isBlank()||!login.isEmpty())&&(!password.isBlank()||!password.isEmpty())){
            if (chatterService.findUserByLogin(login)!=null){
                System.out.println("This user already exists");
            } else {
                chatterService.addNewUser(chatter);
                return "redirect:/login";
            }
        }
        return "redirect:/";
    }

    @GetMapping(value = "/get")
    public String searchChatter(@ModelAttribute("chatter") Chatter chatter, HttpSession session){
        String login = chatter.getLogin();
        String password = chatter.getPasswordHash();
        if ((!login.isBlank()||!login.isEmpty())&&(!password.isBlank()||!password.isEmpty())){
            Chatter pendingChatter = chatterService.findUserByLogin(login);
            if (pendingChatter!=null&&password.equals(pendingChatter.getPasswordHash())){
                    session.setAttribute("chatter",chatter);
                    return "redirect:/main/"+login;
            } else {
                System.out.println("Incorrect login or password");
                return "redirect:/login";
            }
        }
        return "redirect:/login";
    }


}
