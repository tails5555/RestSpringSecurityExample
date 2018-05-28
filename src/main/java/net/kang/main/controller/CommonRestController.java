package net.kang.main.controller;

import net.kang.main.model.DetailVO;
import net.kang.main.model.UserVO;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

// 공동의 사용자(USER, MANAGER, ADMIN)들이 할 수 있는 행위를 구현한 REST Controller 클래스이다.
@RestController
@CrossOrigin
@RequestMapping("common")
public class CommonRestController {
    @Autowired UserService userService;

    // 로그아웃 작업
    @DeleteMapping("logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Principal principal){
        String logoutMessage = String.format("User Logout is Successed -> %s", principal.getName());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new ResponseEntity<String>(logoutMessage, HttpStatus.OK);
    }

    // 본인 정보 확인
    @GetMapping("profile")
    public ResponseEntity<?> profile(Principal principal){
        UserVO userVO = userService.findByUsername(principal.getName());
        if(userVO!=null)
            return new ResponseEntity<UserVO>(userVO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    // 본인 정보 수정
    @PutMapping("update")
    public ResponseEntity<String> update(Principal principal, @RequestBody DetailVO detailVO){
        if(userService.update(principal.getName(), detailVO)){
            return new ResponseEntity<String>(String.format("User Update is Successed -> %s", principal.getName()), HttpStatus.OK);
        }else{
            return new ResponseEntity<String>("User Update is Failured. User is Not Existed.", HttpStatus.NOT_FOUND);
        }
    }

    // 본인 탈퇴
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
