package com.wastonix.dao.impl;

import com.wastonix.config.HibernateUtil;
import com.wastonix.dao.interfaces.FarmerDAO;
import com.wastonix.model.Farmer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class FarmerDAOImpl implements FarmerDAO {
    @Override
    public Farmer save(Farmer f) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction tx = s.beginTransaction();
            s.persist(f); 
            tx.commit();
            return f;
        }
    }

    @Override
    public Farmer findById(Integer id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.find(Farmer.class, id); 
        }
    }

    @Override
    public List<Farmer> findAll() {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("FROM Farmer", Farmer.class).getResultList();
        }
    }

    @Override
    public Farmer update(Farmer f) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction tx = s.beginTransaction();
            s.merge(f);
            tx.commit();
            return f;
        }
    }

    @Override
    public void delete(Integer id) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            Transaction tx = s.beginTransaction();
            Farmer f = s.find(Farmer.class, id); 
            if(f != null) s.remove(f);
            tx.commit();
        }
    }

    @Override
    public List<Farmer> searchByLocation(String loc) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("FROM Farmer WHERE location LIKE :l", Farmer.class)
                    .setParameter("l", "%" + loc + "%").getResultList();
        }
    }

    @Override
    public Farmer findByPhone(String phone) {
        try(Session s = HibernateUtil.getSessionFactory().openSession()){
            return s.createQuery("FROM Farmer WHERE phone = :p", Farmer.class)
                    .setParameter("p", phone).uniqueResult();
        }
    }
}