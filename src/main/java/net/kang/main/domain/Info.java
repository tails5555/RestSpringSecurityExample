package net.kang.main.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Set;

@Data
@Entity
@Table(name="authinfo")
public class Info {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(unique=true)
    String username;

    @JsonIgnore
    String password;

    @JsonIgnore
    @OneToOne(mappedBy="info")
    Detail detail;

    @ManyToMany
    @JoinTable(
            name="infoandrole",
            joinColumns=@JoinColumn(name="infoId"),
            inverseJoinColumns=@JoinColumn(name="roleId"),
            uniqueConstraints= {
                    @UniqueConstraint(
                            columnNames = {"roleId", "infoId"}
                    )
            }
    )
    Set<Role> roles;
}
