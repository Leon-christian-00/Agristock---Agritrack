package com.wastonix.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "farmers")
public class Farmer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @Column(length = 100)
    private String location;

    @Column(name = "farm_size_hectares")
    private Double farmSizeHectares;

    
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Harvest> harvests = new HashSet<>();

    
    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Sale> sales = new HashSet<>();

    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "memberships",
            joinColumns = @JoinColumn(name = "farmer_id"),
            inverseJoinColumns = @JoinColumn(name = "cooperative_id")
    )
    private Set<Cooperative> cooperatives = new HashSet<>();

    public Farmer() {}

    public Farmer(String fullName, String phone, String location, Double farmSizeHectares) {
        this.fullName = fullName;
        this.phone = phone;
        this.location = location;
        this.farmSizeHectares = farmSizeHectares;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getFarmSizeHectares() { return farmSizeHectares; }
    public void setFarmSizeHectares(Double farmSizeHectares) { this.farmSizeHectares = farmSizeHectares; }

    public Set<Harvest> getHarvests() { return harvests; }
    public void setHarvests(Set<Harvest> harvests) { this.harvests = harvests; }

    public Set<Sale> getSales() { return sales; }
    public void setSales(Set<Sale> sales) { this.sales = sales; }

    public Set<Cooperative> getCooperatives() { return cooperatives; }
    public void setCooperatives(Set<Cooperative> cooperatives) { this.cooperatives = cooperatives; }

    
    public void addCooperative(Cooperative cooperative) {
        this.cooperatives.add(cooperative);
        cooperative.getFarmers().add(this);
    }

    public void removeCooperative(Cooperative cooperative) {
        this.cooperatives.remove(cooperative);
        cooperative.getFarmers().remove(this);
    }
}