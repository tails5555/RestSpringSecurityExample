package net.kang.main.model;

import lombok.Data;
import net.kang.main.domain.Detail;
import net.kang.main.domain.Info;

import java.time.LocalDateTime;

@Data
public class SignVO {
    String username;
    String password_1;
    String password_2;
    String name;
    String email;
    LocalDateTime birthday;
    String address;

    public static Detail builtByDetail(Info info, SignVO signVO){
        Detail detail = new Detail();
        detail.setInfo(info);
        detail.setName(signVO.getName());
        detail.setBirthday(signVO.getBirthday());
        detail.setAddress(signVO.getAddress());
        detail.setEmail(signVO.getEmail());
        return detail;
    }
}
