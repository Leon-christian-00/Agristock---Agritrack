package com.wastonix.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "sales")
public class Sale implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String cropName;
    private String buyerName;
    private Double quantitySold;
    private Double unitPrice;
    private Double totalRevenue;
    private LocalDate saleDate;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer; 

    public Sale() {}

    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public Double getQuantitySold() { return quantitySold; }
    public void setQuantitySold(Double quantitySold) { this.quantitySold = quantitySold; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }
    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }
}