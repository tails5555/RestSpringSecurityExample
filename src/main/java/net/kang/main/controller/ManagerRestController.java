package net.kang.main.controller;

import net.kang.main.domain.Role;
import net.kang.main.model.DetailVO;
import net.kang.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("manager")
public class ManagerRestController {
    @Autowired UserService userService;

    @GetMapping("login_process")
    public ResponseEntity<String> loginProcess(){
        return new ResponseEntity<String>("Manager Login is Successed.", HttpStatus.OK);
    }

    @GetMapping("count")
    public ResponseEntity<Map<Role, Long>> counting(){
        Map<Role, Long> countMap = userService.countWithManagerAndUser();
        return new ResponseEntity<>(countMap, HttpStatus.OK);
    }

    @DeleteMapping("delete/{username}")
    public ResponseEntity<String> deleteAnotherUser(@PathVariable("username") String username){
        if(userService.delete(username)){
            return new ResponseEntity<String>(String.format("Another User Delete is Successed -> %s", username), HttpStatus.OK);
        }else{
            return new ResponseEntity<String>("Another User Delete is Failure.", HttpStatus.NOT_FOUND);
        }
    }
}
