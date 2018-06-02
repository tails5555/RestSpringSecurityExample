package net.kang.main.controller;

import net.kang.main.domain.Role;
import net.kang.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

// 매니저(MANAGER)들이 할 수 있는 행위를 구현한 REST Controller 클래스이다.
@RestController
@CrossOrigin
@RequestMapping("manager")
public class ManagerRestController {
    @Autowired UserService userService;

    // 매니저 접근 권한 확인
    @Secured("ROLE_MANAGER")
    @GetMapping("login_process")
    public ResponseEntity<String> loginProcess(){
        return new ResponseEntity<String>("Manager Login is Successed.", HttpStatus.OK);
    }

    // 매니저 수와 일반 회원 수 파악
    @Secured("ROLE_MANAGER")
    @GetMapping("count/role")
    public ResponseEntity<Map<Role, Long>> counting(){
        Map<Role, Long> countMap = userService.countWithManagerAndUser();
        return new ResponseEntity<>(countMap, HttpStatus.OK);
    }

    // 매니저 일반 회원 강퇴
    @Secured("ROLE_MANAGER")
    @DeleteMapping("delete/{username}")
    public ResponseEntity<String> deleteAnotherUser(@PathVariable("username") String username){
        if(userService.deleteForManager(username)){
            return new ResponseEntity<String>(String.format("Another User Delete is Successed -> %s", username), HttpStatus.OK);
        }else{
            return new ResponseEntity<String>("Another User Delete is Failure.", HttpStatus.NOT_FOUND);
        }
    }
}
