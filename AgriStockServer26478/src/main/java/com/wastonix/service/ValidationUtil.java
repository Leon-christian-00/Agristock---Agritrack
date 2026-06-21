package com.wastonix.service;

import com.wastonix.model.Farmer;
import com.wastonix.model.Harvest;
import com.wastonix.model.Sale;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+2507\\d{8}$");

    public static void validateFarmer(Farmer farmer) {
        if (farmer == null) throw new IllegalArgumentException("Farmer cannot be null");

        
        if (farmer.getFullName() != null && farmer.getFullName().length() > 100)
            throw new IllegalArgumentException("Full name cannot exceed 100 characters.");

        
        if (farmer.getPhone() == null || !PHONE_PATTERN.matcher(farmer.getPhone()).matches())
            throw new IllegalArgumentException("Invalid phone format. Use +2507XXXXXXXX.");

        if (farmer.getLocation() == null || farmer.getLocation().trim().isEmpty())
            throw new IllegalArgumentException("Location is required.");
    }

    public static void validateHarvest(Harvest harvest) {
        if (harvest == null) throw new IllegalArgumentException("Harvest cannot be null");

        
        if (harvest.getQuantityKg() == null || harvest.getQuantityKg() <= 0)
            throw new IllegalArgumentException("Harvest quantity must be greater than 0.");

        
        if (harvest.getHarvestDate() != null && harvest.getHarvestDate().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Harvest date cannot be in the future.");
    }

    public static void validateSale(Sale sale) {
        if (sale == null) throw new IllegalArgumentException("Sale cannot be null");

        
        if (sale.getQuantitySold() == null || sale.getQuantitySold() <= 0)
            throw new IllegalArgumentException("Sale quantity must be greater than 0.");

        
        if (sale.getUnitPrice() == null || sale.getUnitPrice() <= 0)
            throw new IllegalArgumentException("Unit price must be greater than 0.");

        
        if (sale.getSaleDate() != null && sale.getSaleDate().isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Sale date cannot be in the future.");
    }

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Email is required.");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches())
            throw new IllegalArgumentException("Invalid email format. Example: user@example.com");
    }
}