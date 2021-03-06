package net.kang.main.model;

import lombok.Data;
import net.kang.main.domain.Detail;
import net.kang.main.domain.Info;
import net.kang.main.domain.Role;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {
    String username;
    String name;
    String email;
    LocalDateTime birthday;
    String address;
    List<Role> roles;
    public UserVO(String username, String name, String email, LocalDateTime birthday, String address, List<Role> roles){
        this.username=username;
        this.name=name;
        this.email=email;
        this.birthday=birthday;
        this.address=address;
        this.roles=roles;
    }
    public static UserVO buildByInfo(Info info){
        Detail detail = info.getDetail();
        UserVO userVO = new UserVO(info.getUsername(), detail.getName(), detail.getEmail(), detail.getBirthday(), detail.getAddress(), info.getRoles());
        return userVO;
    }
}
