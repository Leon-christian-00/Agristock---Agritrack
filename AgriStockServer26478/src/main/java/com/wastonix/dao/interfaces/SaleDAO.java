package com.wastonix.dao.interfaces;

import com.wastonix.model.Sale;
import java.util.List;

public interface SaleDAO {
    Sale save(Sale sale);
    List<Sale> findByFarmerId(Integer farmerId);
    List<Sale> findAll(); 
    double getTotalSoldByFarmerAndCrop(Integer farmerId, String cropName);
}