package com.wastonix.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "memberships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"farmer_id", "cooperative_id"}))
public class Membership implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private Cooperative cooperative;

    @Column(length = 50)
    private String role = "MEMBER";

    @Column(name = "joined_date")
    private LocalDate joinedDate;

    
    public Membership() {
        this.joinedDate = LocalDate.now();
    }

    public Membership(Farmer farmer, Cooperative cooperative, String role) {
        this.farmer = farmer;
        this.cooperative = cooperative;
        this.role = role != null ? role : "MEMBER";
        this.joinedDate = LocalDate.now();
    }

    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public Cooperative getCooperative() { return cooperative; }
    public void setCooperative(Cooperative cooperative) { this.cooperative = cooperative; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }

    @Override
    public String toString() {
        return "Membership{id=" + id + ", farmer=" + (farmer != null ? farmer.getFullName() : "null") +
                ", cooperative=" + (cooperative != null ? cooperative.getName() : "null") +
                ", role='" + role + "'}";
    }
}