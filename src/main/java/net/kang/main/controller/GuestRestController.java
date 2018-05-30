package net.kang.main.controller;

import net.kang.main.component.AuthProvider;
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

// 비회원이 할 수 있는 행위를 구현한 REST Controller 클래스이다.
@RestController
@CrossOrigin
@RequestMapping("guest")
public class GuestRestController {
    @Autowired UserService userService;
    @Autowired AuthProvider authProvider;

    // 현재 비회원으로 접속할 때 HELLO WORLD 정도 출력하는 실험.
    @GetMapping("main")
    public ResponseEntity<String> main(){
        return new ResponseEntity<String>("Welcome To Spring Security Test. Please Login!", HttpStatus.OK);
    }

    // 회원 가입 작업
    @PostMapping("sign")
    public ResponseEntity<String> sign(@RequestBody SignVO signVO){
        if(userService.create(signVO)){
            return new ResponseEntity<String>("User Create is Successed.", HttpStatus.CREATED);
        }else{
            return new ResponseEntity<String>("User Create is Failured. Password is Wrong...", HttpStatus.NOT_MODIFIED);
        }
    }

    // 본인 이름과 E-Mail로 회원 이름 정보 탐색.
    // Postman은 GET Method에서 RequestBody를 제공하지 않는 단점이 있어서 POST 방식으로 주고 받음.
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
