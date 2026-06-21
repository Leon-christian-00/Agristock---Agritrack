package com.wastonix.dao.interfaces;

import com.wastonix.model.Farmer;
import java.util.List;

public interface FarmerDAO {
    Farmer save(Farmer farmer);
    Farmer findById(Integer id);
    List<Farmer> findAll();
    Farmer update(Farmer farmer);
    void delete(Integer id);
    List<Farmer> searchByLocation(String location);
    Farmer findByPhone(String phone);
}