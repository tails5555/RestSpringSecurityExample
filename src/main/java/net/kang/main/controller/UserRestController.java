package net.kang.main.controller;

import net.kang.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 일반 사용자(USER)들이 할 수 있는 행위를 구현한 REST Controller 클래스이다.
@RestController
@Secured("ROLE_USER")
@RequestMapping("user")
public class UserRestController {
    @Autowired UserService userService;

    // USER ROLE로 접속할 때 HELLO WORLD 정도 출력하는 실험.
    @GetMapping("login_process")
    public ResponseEntity<String> loginProcess(){
        return new ResponseEntity<String>("User Login is Successed.", HttpStatus.OK);
    }
}
