package net.kang.main.controller;

import net.kang.main.model.DetailVO;
import net.kang.main.model.UserVO;
import net.kang.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Secured("ROLE_USER")
@RequestMapping("user")
public class UserRestController {
    @Autowired UserService userService;

    @GetMapping("login_process")
    public ResponseEntity<String> loginProcess(){
        return new ResponseEntity<String>("Login is Successed.", HttpStatus.OK);
    }

    @GetMapping("user_profile")
    public ResponseEntity<UserVO> userProfile(Principal principal){
        UserVO userVO = userService.findByUsername(principal.getName());
        return new ResponseEntity<UserVO>(userVO, HttpStatus.OK);
    }

    @GetMapping("logout")
    public ResponseEntity<String> logout(){
        return new ResponseEntity<String>("Logout is Successed.", HttpStatus.OK);
    }

    @PutMapping("user_update")
    public ResponseEntity<String> userUpdate(Principal principal, @RequestBody DetailVO detailVO){
        if(userService.update(principal.getName(), detailVO)){
            return new ResponseEntity<String>("User Update is Successed.", HttpStatus.OK);
        }else{
            return new ResponseEntity<String>("User Update is Failured. User is Not Existed.", HttpStatus.NOT_FOUND);
        }
    }
}
