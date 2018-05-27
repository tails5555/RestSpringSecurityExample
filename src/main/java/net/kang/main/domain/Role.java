package net.kang.main.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;

@Data
@ToString(exclude={"infos"})
@Entity
@Table(name="authrole")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(unique=true)
    String name;

    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
            name="infoandrole",
            joinColumns=@JoinColumn(name="roleId"),
            inverseJoinColumns=@JoinColumn(name="infoId"),
            uniqueConstraints= {
                    @UniqueConstraint(
                            columnNames = {"roleId", "infoId"}
                    )
            }
    )
    List<Info> infos;
}
