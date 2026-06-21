package com.wastonix.dao.impl;

import com.wastonix.config.HibernateUtil;
import com.wastonix.dao.interfaces.SaleDAO;
import com.wastonix.model.Sale;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class SaleDAOImpl implements SaleDAO {
    @Override
    public Sale save(Sale s) {
        try(Session sess=HibernateUtil.getSessionFactory().openSession()){
            Transaction tx=sess.beginTransaction();
            sess.persist(s);
            tx.commit();
            return s;
        }
    }

    @Override
    public List<Sale> findByFarmerId(Integer fid) {
        try(Session s=HibernateUtil.getSessionFactory().openSession()){
            
            return s.createQuery(
                    "SELECT s FROM Sale s JOIN FETCH s.farmer WHERE s.farmer.id=:fid",
                    Sale.class
            ).setParameter("fid", fid).getResultList();
        }
    }

    @Override
    public List<Sale> findAll() {
        try(Session s=HibernateUtil.getSessionFactory().openSession()){
            
            return s.createQuery(
                    "SELECT s FROM Sale s JOIN FETCH s.farmer",
                    Sale.class
            ).getResultList();
        }
    }

    @Override
    public double getTotalSoldByFarmerAndCrop(Integer fid, String crop) {
        try(Session s=HibernateUtil.getSessionFactory().openSession()){
            Double res = s.createQuery(
                    "SELECT SUM(s.quantitySold) FROM Sale s WHERE s.farmer.id=:fid AND s.cropName=:crop",
                    Double.class
            ).setParameter("fid", fid).setParameter("crop", crop).uniqueResult();
            return res != null ? res : 0.0;
        }
    }
}