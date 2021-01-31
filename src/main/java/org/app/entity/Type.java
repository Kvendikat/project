package org.app.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Table(name = "types")
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(length = 30)
    @Size(min = 2, max = 30, message = "Типоразмер не может быть меньше 2 знаков и больше 30")
    @NotEmpty(message = "Типоразмер не должно быть пустым")
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
