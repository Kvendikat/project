package org.app.entity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "plates")
public class Plate {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 30)
    @NotEmpty(message = "Поле не может быть пустым")
    @Size(min = 2, max = 25, message = "Номер заготовки не должен быть меньше 2 знаков и больше 25")
    private String number;

    @Column
    @NotNull(message = "Поле не может быть пустым")
    @Min(value = 1, message = "Длина должна быть больше нуля")
    private Integer length;

    @Column
    @NotNull(message = "Поле не может быть пустым")
    @Min(value = 1, message = "Ширина должна быть больше нуля")
    private Integer width;

    @Column
    @Min(value = 1, message = "Высота должна быть больше нуля")
    @NotNull(message = "Поле не может быть пустым")
    private Integer height;

    @Column (name = "param_one")
    @NotNull(message = "Поле не может быть пустым")
    private Double paramOne;

    @Column (name = "param_two")
    @NotNull(message = "Поле не может быть пустым")
    private Double paramTwo;

    @Column (name = "in_storage", columnDefinition = "bit")
    private Boolean inStorage = true;

    @Column (name = "has_crack", columnDefinition = "bit")
    private Boolean hasCrack = false;

    @Column
    private String note;

    @Column (name = "crash", columnDefinition = "bit")
    private Boolean crash = false;

    @Column (name = "archive", columnDefinition = "bit")
    private Boolean archive = false;

    @OneToOne
    @JoinColumn(name = "what_for_id", referencedColumnName = "id")
    private WhatFor whatFor;

    @ManyToOne(targetEntity = Protocol.class)
    @JoinColumn(name = "protocol_id", referencedColumnName = "id")
    private Protocol protocol;

    @OneToOne
    @JoinColumn(name = "types_id", referencedColumnName = "id")
    private Type type;
    // типоразмер

    @OneToOne
    @JoinColumn(name = "materials_id", referencedColumnName = "id")
    private Material material;

    public Integer getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getParamOne() {
        return paramOne;
    }

    public void setParamOne(Double strainInWork) {
        this.paramOne = strainInWork;
    }

    public Double getParamTwo() {
        return paramTwo;
    }

    public void setParamTwo(Double strainInNotWork) {
        this.paramTwo = strainInNotWork;
    }

    public Boolean getInStorage() {
        return inStorage;
    }

    public void setInStorage(Boolean inStorage) {
        this.inStorage = inStorage;
    }

    public Boolean getHasCrack() {
        return hasCrack;
    }

    public void setHasCrack(Boolean hasStrain) {
        this.hasCrack = hasStrain;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getCrash() {
        return crash;
    }

    public void setCrash(Boolean crash) {
        this.crash = crash;
    }

    public WhatFor getWhatFor() {
        return whatFor;
    }

    public void setWhatFor(WhatFor whatFor) {
        this.whatFor = whatFor;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Boolean getArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
