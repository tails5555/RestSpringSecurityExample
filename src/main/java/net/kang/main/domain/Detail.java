package net.kang.main.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="authdetail")
public class Detail {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    long id;

    String name;
    String address;

    @Column(unique=true)
    String email;

    LocalDateTime birthday;

    @OneToOne
    @JoinColumn(name="authInfoId")
    Info info;
}
