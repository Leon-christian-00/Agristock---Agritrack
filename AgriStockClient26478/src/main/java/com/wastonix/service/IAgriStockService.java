package com.wastonix.service;

import com.wastonix.model.Farmer;
import com.wastonix.model.Harvest;
import com.wastonix.model.Sale;
import com.wastonix.model.User;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IAgriStockService extends Remote {
    
    void registerUser(String fullName, String email, String phone) throws RemoteException;
    void requestOTP(String email) throws RemoteException;
    boolean verifyOTP(String email, String code) throws RemoteException;
    String getUserRole(String email) throws RemoteException;

    
    List<User> getPendingUsers() throws RemoteException;
    void approveUser(Integer userId) throws RemoteException;
    void rejectUser(Integer userId) throws RemoteException;
    
    
    User findUserById(Integer userId) throws RemoteException;
    List<User> findAllUsers() throws RemoteException;
    User saveUser(User user) throws RemoteException;
    User updateUser(User user) throws RemoteException;
    void deleteUser(Integer userId) throws RemoteException;

    
    Farmer saveFarmer(Farmer farmer) throws RemoteException;
    Farmer updateFarmer(Farmer farmer) throws RemoteException;
    void deleteFarmer(Integer id) throws RemoteException;
    Farmer findFarmerById(Integer id) throws RemoteException;
    List<Farmer> findAllFarmers() throws RemoteException;
    List<Farmer> searchFarmersByLocation(String location) throws RemoteException;

    
    Harvest saveHarvest(Harvest harvest) throws RemoteException;
    List<Harvest> findHarvestsByFarmerId(Integer farmerId) throws RemoteException;
    List<Harvest> findAllHarvests() throws RemoteException;

    
    Sale saveSale(Sale sale) throws RemoteException;
    List<Sale> findSalesByFarmerId(Integer farmerId) throws RemoteException;
    List<Sale> findAllSales() throws RemoteException;

    
    byte[] generateHarvestReportPDF(Integer farmerId) throws RemoteException;
    String exportHarvestsToCSV(Integer farmerId) throws RemoteException;
}