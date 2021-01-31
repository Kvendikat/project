package org.app.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "what_for")
public class WhatFor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @Size(min = 2, max = 30, message = "Назначение не должно быть меньше 2 знаков и больше 30")
    @NotEmpty(message = "Применение не должно быть пустым")
    private String type;

    @Column
    @NotNull(message = "Количество изготавливаемых деталей не должно быть пустым")
    @Min(value = 1, message = "Количество изготавливаемых деталей не должно быть меньше 1")
    private Integer quantity;

    @Column(name = "active", columnDefinition = "bit")
    private Boolean active = true;

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
