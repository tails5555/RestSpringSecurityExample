package net.kang.main.controller;

import net.kang.main.model.NameEmailVO;
import net.kang.main.model.SignVO;
import net.kang.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("guest")
public class GuestRestController {
    @Autowired UserService userService;

    @GetMapping("main")
    public ResponseEntity<String> main(){
        return new ResponseEntity<String>("Welcome To Spring Security Test", HttpStatus.OK);
    }

    @PostMapping("sign")
    public ResponseEntity<String> sign(@RequestBody SignVO signVO){
        if(userService.create(signVO)){
            return new ResponseEntity<String>("User Create is Successed.", HttpStatus.CREATED);
        }else{
            return new ResponseEntity<String>("User Create is Failured. Password is Wrong..", HttpStatus.NOT_MODIFIED);
        }
    }

    @PostMapping("find_username")
    public ResponseEntity<String> findUsername(@RequestBody NameEmailVO nameEmailVO){
        String context = userService.findUsername(nameEmailVO);
        if(context!=null){
            return new ResponseEntity<String>(context, HttpStatus.OK);
        }else{
            return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
        }
    }
}
