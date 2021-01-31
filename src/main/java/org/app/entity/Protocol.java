package org.app.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "protocols")
public class Protocol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(length = 30)
    private String number;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @Column
    private String note;

    @OneToOne
    @NotNull(message = "Выберите производителя")
    @JoinColumn(name = "producer_id", referencedColumnName = "id")
    private Producer producer;

    @OneToOne
    @JoinColumn(name = "laboratory_boss_id", referencedColumnName = "id")
    private LaboratoryBoss laboratoryBoss;

    @OneToOne
    @JoinColumn(name = "department_boss_id", referencedColumnName = "id")
    private DepartmentBoss departmentBoss;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany (targetEntity = Plate.class, mappedBy = "protocol")
    @OrderBy("length ASC ")
    private List<Plate> plates;

    public Integer getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public LaboratoryBoss getLaboratoryBoss() {
        return laboratoryBoss;
    }

    public void setLaboratoryBoss(LaboratoryBoss laboratoryBoss) {
        this.laboratoryBoss = laboratoryBoss;
    }

    public DepartmentBoss getDepartmentBoss() {
        return departmentBoss;
    }

    public void setDepartmentBoss(DepartmentBoss departmentBoss) {
        this.departmentBoss = departmentBoss;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Plate> getPlates() {
        return plates;
    }

    public void setPlates(List<Plate> plates) {
        this.plates = plates;
    }
}
