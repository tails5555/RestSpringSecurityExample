package net.kang.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("guest")
public class GuestRestController {
    @GetMapping("main")
    public ResponseEntity<String> main(){
        return new ResponseEntity<String>("Welcome To Spring Security Test", HttpStatus.OK);
    }
}
