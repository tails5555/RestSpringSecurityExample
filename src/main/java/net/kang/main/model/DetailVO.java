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
}
