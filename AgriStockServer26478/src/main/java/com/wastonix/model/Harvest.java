package com.wastonix.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "harvests")
public class Harvest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String cropName;
    private Double quantityKg;
    private String qualityGrade = "Standard";
    private LocalDate harvestDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    
    public Integer getId() { return id; } public void setId(Integer id) { this.id = id; }
    public String getCropName() { return cropName; } public void setCropName(String cropName) { this.cropName = cropName; }
    public Double getQuantityKg() { return quantityKg; } public void setQuantityKg(Double quantityKg) { this.quantityKg = quantityKg; }
    public String getQualityGrade() { return qualityGrade; } public void setQualityGrade(String qualityGrade) { this.qualityGrade = qualityGrade; }
    public LocalDate getHarvestDate() { return harvestDate; } public void setHarvestDate(LocalDate harvestDate) { this.harvestDate = harvestDate; }
    public Farmer getFarmer() { return farmer; } public void setFarmer(Farmer farmer) { this.farmer = farmer; }
}