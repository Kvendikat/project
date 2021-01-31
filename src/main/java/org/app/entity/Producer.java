package org.app.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "producers")
public class Producer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(length = 40)
    @NotEmpty(message = "Название не должено быть пустым")
    private String name;

    @Column
    @Pattern(regexp = "[\\D]+", message = "ФИО не должно содержать цифр")
    private String surname;

    @Column
    private String position;

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
