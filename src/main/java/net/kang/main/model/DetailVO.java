package net.kang.main.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DetailVO {
    String beforePassword;
    String newPassword;
    String address;
    String email;
    LocalDateTime birthday;
    public DetailVO(){

    }
    public DetailVO(String beforePassword, String newPassword, String address, String email, LocalDateTime birthday){
        this.beforePassword=beforePassword;
        this.newPassword=newPassword;
        this.address=address;
        this.email=email;
        this.birthday=birthday;
    }
}
