package net.kang.main.controller;

import net.kang.main.domain.Role;
import net.kang.main.model.UserVO;
import net.kang.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import java.util.List;
import java.util.Map;

// 관리자(ADMIN)들이 할 수 있는 행위를 구현한 REST Controller 클래스이다.
@RestController
@CrossOrigin
@RequestMapping("admin")
public class AdminRestController {
    @Autowired UserService userService;

    // 관리자 접근 확인
    @Secured("ROLE_ADMIN")
    @GetMapping("login_process")
    public ResponseEntity<String> loginProcess(){
        return new ResponseEntity<String>("Admin Login is Successed.", HttpStatus.OK);
    }

    // 관리자 모든 회원 조회
    @Secured("ROLE_ADMIN")
    @GetMapping("all_users")
    public ResponseEntity<List<UserVO>> allUsers(){
        List<UserVO> userVOList = userService.findAll();
        if(userVOList.size()>0) return new ResponseEntity<List<UserVO>>(userVOList, HttpStatus.OK);
        else return new ResponseEntity<List<UserVO>>(userVOList, HttpStatus.NOT_FOUND);
    }

    // 관리자 회원 수 파악
    @Secured("ROLE_ADMIN")
    @GetMapping("count")
    public ResponseEntity<Long> counting(){
        long result = userService.count();
        if(result>0) return new ResponseEntity<Long>(result, HttpStatus.OK);
        else return new ResponseEntity<Long>(0L, HttpStatus.NOT_FOUND);
    }

    // 관리자 권한 별 회원 수 파악
    @Secured("ROLE_ADMIN")
    @GetMapping("count/role")
    public ResponseEntity<Map<Role, Long>> countingWithRole(){
        Map<Role, Long> countMap = userService.countWithAll();
        return new ResponseEntity<>(countMap, HttpStatus.OK);
    }

    // 관리자 회원 강퇴
    @Secured("ROLE_ADMIN")
    @DeleteMapping("delete/{username}")
    public ResponseEntity<String> deleteAnotherUser(@PathVariable("username") String username){
        if(userService.delete(username)){
            return new ResponseEntity<String>(String.format("Another User Delete is Successed -> %s", username), HttpStatus.OK);
        }else{
            return new ResponseEntity<String>("Another User Delete is Failure.", HttpStatus.NOT_FOUND);
        }
    }

    // 관리자 회원 권한 부여
    @Secured("ROLE_ADMIN")
    @PutMapping("role_grant/{username}/{role}")
    public ResponseEntity<String> roleGrant(@PathVariable("username") String username, @PathVariable String role) throws ServletException{
        userService.roleUpdate(username, role, true);
        return new ResponseEntity<String>(String.format("User Role Granting is Complete -> Role : %s / User Name : %s", role, username), HttpStatus.OK);
    }

    // 관리자 회원 권한 회수
    @Secured("ROLE_ADMIN")
    @PutMapping("role_revoke/{username}/{role}")
    public ResponseEntity<String> roleRevoke(@PathVariable("username") String username, @PathVariable String role) throws ServletException {
        userService.roleUpdate(username, role, false);
        return new ResponseEntity<String>(String.format("User Role Revoking is Complete -> Role : %s / User Name : %s", role, username), HttpStatus.OK);
    }
}
