package com.wastonix.dao.interfaces;

import com.wastonix.model.Harvest;
import java.util.List;

public interface HarvestDAO {
    Harvest save(Harvest harvest);
    List<Harvest> findByFarmerId(Integer farmerId);
    List<Harvest> findAll(); 
    double getTotalQuantityByFarmerAndCrop(Integer farmerId, String cropName);
}