package com.wastonix.client.util;

import com.wastonix.service.IAgriStockService;
import java.rmi.Naming;

public class RmiClientUtil {
    private static final String RMI_URL = "//localhost:5000/AgriStockService";
    private static IAgriStockService service;

    public static IAgriStockService getService() {
        if (service == null) {
            try {
                service = (IAgriStockService) Naming.lookup(RMI_URL);
                System.out.println("✅ Connected to AgriStock RMI Service");
            } catch (Exception e) {
                System.err.println("❌ RMI Connection failed: " + e.getMessage());
                throw new RuntimeException("Cannot connect to server", e);
            }
        }
        return service;
    }

    public static void reconnect() {
        service = null; 
        getService();
    }
}