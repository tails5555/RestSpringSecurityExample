package net.kang.main.controller;

import net.kang.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Secured("ROLE_USER")
@RequestMapping("user")
public class UserRestController {
    @Autowired UserService userService;

    @GetMapping("login_process")
    public String loginProcess(){
        return "Login is Successed.";
    }

    @GetMapping("logout")
    public String logout(){
        return "Logout is Successed.";
    }
}
