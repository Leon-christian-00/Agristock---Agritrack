package com.wastonix.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cooperatives")
public class Cooperative implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String location;

    @Column(name = "registration_number", unique = true, length = 50)
    private String registrationNumber;

    
    @ManyToMany(mappedBy = "cooperatives")
    private transient Set<Farmer> farmers = new HashSet<>();

    public Cooperative() {}

    public Cooperative(String name, String location, String registrationNumber) {
        this.name = name;
        this.location = location;
        this.registrationNumber = registrationNumber;
    }

    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public Set<Farmer> getFarmers() { return farmers; }
    public void setFarmers(Set<Farmer> farmers) { this.farmers = farmers; }
}