package net.kang.main.model;

import lombok.Data;
import net.kang.main.domain.Detail;
import net.kang.main.domain.Info;

import java.time.LocalDateTime;

@Data
public class UserVO {
    String username;
    String name;
    String email;
    LocalDateTime birthday;
    String address;
    public UserVO(String username, String name, String email, LocalDateTime birthday, String address){
        this.username=username;
        this.name=name;
        this.email=email;
        this.birthday=birthday;
        this.address=address;
    }
    public static UserVO buildByInfo(Info info){
        Detail detail = info.getDetail();
        UserVO userVO = new UserVO(info.getUsername(), detail.getName(), detail.getEmail(), detail.getBirthday(), detail.getAddress());
        return userVO;
    }
}
