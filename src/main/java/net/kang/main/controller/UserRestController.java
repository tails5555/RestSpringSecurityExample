package net.kang.main.controller;

import net.kang.main.model.DetailVO;
import net.kang.main.model.UserVO;
import net.kang.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@RestController
@Secured("ROLE_USER")
@RequestMapping("user")
public class UserRestController {
    @Autowired UserService userService;

    @GetMapping("login_process")
    public ResponseEntity<String> loginProcess(){
        return new ResponseEntity<String>("User Login is Successed.", HttpStatus.OK);
    }

    @GetMapping("profile")
    public ResponseEntity<?> profile(Principal principal){
        UserVO userVO = userService.findByUsername(principal.getName());
        if(userVO!=null)
            return new ResponseEntity<UserVO>(userVO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new ResponseEntity<String>("User Logout is Successed.", HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<String> update(Principal principal, @RequestBody DetailVO detailVO){
        if(userService.update(principal.getName(), detailVO)){
            return new ResponseEntity<String>("User Update is Successed.", HttpStatus.OK);
        }else{
            return new ResponseEntity<String>("User Update is Failured. User is Not Existed.", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> delete(HttpServletRequest request, HttpServletResponse response, Principal principal){
        if(userService.delete(principal.getName())){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null){
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            return new ResponseEntity<String>(String.format("User Delete is Successed -> %s", principal.getName()), HttpStatus.OK);
        }else{
            return new ResponseEntity<String>("User Delete is Failure.", HttpStatus.NOT_FOUND);
        }
    }
}
