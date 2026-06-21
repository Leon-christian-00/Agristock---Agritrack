package com.wastonix.dao.impl;

import com.wastonix.config.HibernateUtil;
import com.wastonix.dao.interfaces.HarvestDAO;
import com.wastonix.model.Harvest;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class HarvestDAOImpl implements HarvestDAO {
    @Override
    public Harvest save(Harvest h) {
        try(Session s=HibernateUtil.getSessionFactory().openSession()){
            Transaction tx=s.beginTransaction();
            s.persist(h);
            tx.commit();
            return h;
        }
    }

    @Override
    public List<Harvest> findByFarmerId(Integer fid) {
        try(Session s=HibernateUtil.getSessionFactory().openSession()){
            
            return s.createQuery(
                    "SELECT h FROM Harvest h JOIN FETCH h.farmer WHERE h.farmer.id=:fid",
                    Harvest.class
            ).setParameter("fid", fid).getResultList();
        }
    }

    @Override
    public List<Harvest> findAll() {
        try(Session s=HibernateUtil.getSessionFactory().openSession()){
            
            return s.createQuery(
                    "SELECT h FROM Harvest h JOIN FETCH h.farmer",
                    Harvest.class
            ).getResultList();
        }
    }

    @Override
    public double getTotalQuantityByFarmerAndCrop(Integer fid, String crop) {
        try(Session s=HibernateUtil.getSessionFactory().openSession()){
            Double res = s.createQuery(
                    "SELECT SUM(h.quantityKg) FROM Harvest h WHERE h.farmer.id=:fid AND h.cropName=:crop",
                    Double.class
            ).setParameter("fid", fid).setParameter("crop", crop).uniqueResult();
            return res != null ? res : 0.0;
        }
    }
}