package org.AshInc.controller;

import org.AshInc.model.Chatter;
import org.AshInc.model.Room;
import org.AshInc.service.ChatterService;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ChatterController {
    @Autowired
    private ChatterService chatterService;

    @PostMapping(value = "/add")
    public String addNewChatter(@ModelAttribute("chatter") Chatter chatter, RedirectAttributes redirectAttributes){
//        session.setAttribute("chatter",chatter);
        String login = StringEscapeUtils.escapeHtml4(chatter.getLogin());
        String password = StringEscapeUtils.escapeHtml4(chatter.getPasswordHash());
        System.out.println(login);
        System.out.println(password);
        if ((!login.isBlank()||!login.isEmpty())&&(!password.isBlank()||!password.isEmpty())){
            if (chatterService.findUserByLogin(login)!=null){
                redirectAttributes.addFlashAttribute("error","This user already exists");
                return "redirect:/signup";
            } else {
                Pattern loginPattern = Pattern.compile("^[a-zA-Z0-9_-]{3,32}$");
                Matcher loginMatcher = loginPattern.matcher(login);
                Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9]{5,128}$");
                Matcher passwordMatcher = passwordPattern.matcher(password);
                if (loginMatcher.matches()&&passwordMatcher.matches()){
                    chatter.setLogin(login);
                    chatter.setPasswordHash(password);
                    chatterService.addNewUser(chatter);
                    return "redirect:/login";
                } else {
                    redirectAttributes.addFlashAttribute("error","Invalid username or password\nFor login: 3-16 characters, dashes and underscores are allowed\nFor password: 5-128 characters or numbers");
                    return "redirect:/signup";
                }
            }
        }
        redirectAttributes.addFlashAttribute("error","Empty inputs are not allowed");
        return "redirect:/signup";
    }

    @GetMapping(value = "/get")
    public String searchChatter(@ModelAttribute("chatter") Chatter chatter, HttpSession session, RedirectAttributes redirectAttributes){
        String login = StringEscapeUtils.escapeHtml4(chatter.getLogin());
        String password = StringEscapeUtils.escapeHtml4(chatter.getPasswordHash());
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
