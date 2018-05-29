package net.kang.main.controller;

import net.kang.main.domain.Role;
import net.kang.main.model.UserVO;
import net.kang.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// 관리자(ADMIN)들이 할 수 있는 행위를 구현한 REST Controller 클래스이다.
@RestController
@CrossOrigin
@RequestMapping("admin")
public class AdminRestController {
    @Autowired UserService userService;

    @GetMapping("login_process")
    public ResponseEntity<String> loginProcess(){
        return new ResponseEntity<String>("Admin Login is Successed.", HttpStatus.OK);
    }

    @GetMapping("all_users")
    public ResponseEntity<List<UserVO>> allUsers(){
        List<UserVO> userVOList = userService.findAll();
        if(userVOList.size()>0) return new ResponseEntity<List<UserVO>>(userVOList, HttpStatus.OK);
        else return new ResponseEntity<List<UserVO>>(userVOList, HttpStatus.NOT_FOUND);
    }

    @GetMapping("count")
    public ResponseEntity<Long> counting(){
        long result = userService.count();
        if(result>0) return new ResponseEntity<Long>(result, HttpStatus.OK);
        else return new ResponseEntity<Long>(0L, HttpStatus.NOT_FOUND);
    }

    @GetMapping("count/role")
    public ResponseEntity<Map<Role, Long>> countingWithRole(){
        Map<Role, Long> countMap = userService.countWithAll();
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
