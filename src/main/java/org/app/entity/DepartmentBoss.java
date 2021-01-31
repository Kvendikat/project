package org.app.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "department_boss")
public class DepartmentBoss {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(length = 30)
    @NotEmpty(message = "ФИО не должно быть пустым")
    @Pattern(regexp = "[\\D]+", message = "ФИО не должно содержать цифр")
    private String name;

    @Column(name = "active", columnDefinition = "bit")
    private Boolean active = true;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
