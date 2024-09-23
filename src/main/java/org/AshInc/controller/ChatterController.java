package org.AshInc.controller;

import org.AshInc.model.Chatter;
import org.AshInc.model.Room;
import org.AshInc.service.ChatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
@Controller
public class ChatterController {
    @Autowired
    private ChatterService chatterService;

    @PostMapping(value = "/add")
    public String addNewChatter(@ModelAttribute("chatter") Chatter chatter, RedirectAttributes redirectAttributes){
//        session.setAttribute("chatter",chatter);
        String login = chatter.getLogin();
        String password = chatter.getPasswordHash();
        if ((!login.isBlank()||!login.isEmpty())&&(!password.isBlank()||!password.isEmpty())){
            if (chatterService.findUserByLogin(login)!=null){
                redirectAttributes.addFlashAttribute("error","This user already exists");
                System.out.println("This user already exists");
                return "redirect:/signup";
            } else {
                chatterService.addNewUser(chatter);
                return "redirect:/login";
            }
        }
        redirectAttributes.addFlashAttribute("error","Empty inputs are not allowed");
        return "redirect:/signup";
    }

    @GetMapping(value = "/get")
    public String searchChatter(@ModelAttribute("chatter") Chatter chatter, HttpSession session, RedirectAttributes redirectAttributes){
        String login = chatter.getLogin();
        String password = chatter.getPasswordHash();
        if ((!login.isBlank()||!login.isEmpty())&&(!password.isBlank()||!password.isEmpty())){
            Chatter pendingChatter = chatterService.findUserByLogin(login);
            if (pendingChatter!=null&&password.equals(pendingChatter.getPasswordHash())){
                    session.setAttribute("chatter",chatter);
                    return "redirect:/main/"+login;
            } else {
                System.out.println("Incorrect login or password");
                redirectAttributes.addFlashAttribute("error", "Incorrect login or password");
                return "redirect:/login";
            }
        }
        redirectAttributes.addFlashAttribute("error", "Empty inputs are not allowed");
        return "redirect:/login";
    }


}
