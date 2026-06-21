package com.wastonix.service;

import com.wastonix.config.HibernateUtil;
import com.wastonix.dao.impl.FarmerDAOImpl;
import com.wastonix.dao.impl.HarvestDAOImpl;
import com.wastonix.dao.impl.SaleDAOImpl;
import com.wastonix.dao.interfaces.FarmerDAO;
import com.wastonix.dao.interfaces.HarvestDAO;
import com.wastonix.dao.interfaces.SaleDAO;
import com.wastonix.model.*;
import com.wastonix.notification.EmailService;
import com.wastonix.notification.NotificationPublisher;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class AgriStockServiceImpl extends UnicastRemoteObject implements IAgriStockService {
    private final FarmerDAO farmerDAO;
    private final HarvestDAO harvestDAO;
    private final SaleDAO saleDAO;
    private final NotificationPublisher notifier;

    
    private static final String ADMIN_EMAIL = "wastonorganisation@gmail.com";

    private final Map<String, OTPSession> otpStore = new ConcurrentHashMap<>();

    public AgriStockServiceImpl() throws RemoteException {
        super();
        this.farmerDAO = new FarmerDAOImpl();
        this.harvestDAO = new HarvestDAOImpl();
        this.saleDAO = new SaleDAOImpl();
        this.notifier = new NotificationPublisher();
    }

    private static class OTPSession {
        String code;
        LocalDateTime expiry;
        OTPSession(String code, int minutes) {
            this.code = code;
            this.expiry = LocalDateTime.now().plusMinutes(minutes);
        }
    }

    

    @Override
    public void registerUser(String fullName, String email, String phone) throws RemoteException {
        try { ValidationUtil.validateEmail(email); }
        catch (IllegalArgumentException e) { throw new RemoteException(e.getMessage()); }
        if (fullName == null || fullName.trim().isEmpty())
            throw new RemoteException("Full name is required.");
        if (phone == null || !phone.matches("^\\+2507\\d{8}$"))
            throw new RemoteException("Phone format must be +2507XXXXXXXX.");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            User existing = session.createQuery("FROM User WHERE email = :e", User.class)
                .setParameter("e", email.trim()).uniqueResult();
            if (existing != null) throw new RemoteException("Email is already registered.");

            
            User existingPhone = session.createQuery("FROM User WHERE phone = :p", User.class)
                .setParameter("p", phone.trim()).uniqueResult();
            if (existingPhone != null) throw new RemoteException("Phone number is already registered.");

            
            Transaction tx = session.beginTransaction();
            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmail(email.trim());
            user.setPhone(phone.trim());
            user.setApprovalStatus(User.ApprovalStatus.PENDING);
            user.setActive(false); 
            session.persist(user);
            tx.commit();

            
            EmailService.sendAdminNewRegistrationAlert(ADMIN_EMAIL, fullName.trim(), email.trim());
            notifier.publish("USER_REGISTERED", "New registration pending: " + email);
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Registration failed: " + e.getMessage());
        }
    }

    

    @Override
    public void requestOTP(String email) throws RemoteException {
        try { ValidationUtil.validateEmail(email); }
        catch (IllegalArgumentException e) { throw new RemoteException(e.getMessage()); }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.createQuery("FROM User WHERE email = :e", User.class)
                .setParameter("e", email.trim()).uniqueResult();

            if (user == null)
                throw new RemoteException("No account found with this email. Please register first.");

            if (user.getApprovalStatus() == User.ApprovalStatus.PENDING)
                throw new RemoteException("Your account is pending admin approval. You will be notified by email once approved.");

            if (user.getApprovalStatus() == User.ApprovalStatus.REJECTED)
                throw new RemoteException("Your registration was not approved. Please contact the administrator.");

            if (!user.isActive())
                throw new RemoteException("Your account has been deactivated. Contact the administrator.");

            String code = String.format("%06d", new Random().nextInt(999999));
            otpStore.put(email.trim(), new OTPSession(code, 5));

            
            EmailService.sendOTP(email.trim(), code);
            notifier.publish("OTP_SENT", "OTP emailed to: " + email);
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error requesting OTP: " + e.getMessage());
        }
    }

    

    @Override
    public boolean verifyOTP(String email, String code) throws RemoteException {
        OTPSession session = otpStore.get(email.trim());
        if (session == null || LocalDateTime.now().isAfter(session.expiry)) {
            otpStore.remove(email.trim());
            throw new RemoteException("OTP has expired. Please request a new one.");
        }
        if (session.code.equals(code.trim())) {
            otpStore.remove(email.trim());
            notifier.publish("LOGIN_SUCCESS", "User logged in: " + email);
            return true;
        }
        throw new RemoteException("Incorrect OTP code. Please try again.");
    }

    

    @Override
    public String getUserRole(String email) throws RemoteException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.createQuery("FROM User WHERE email = :e", User.class)
                .setParameter("e", email.trim()).uniqueResult();
            if (user == null) return "OFFICER";
            if (user.getProfile() != null) return user.getProfile().getRole();
            return "OFFICER";
        } catch (Exception e) {
            throw new RemoteException("Error fetching role: " + e.getMessage());
        }
    }

    

    @Override
    public List<User> getPendingUsers() throws RemoteException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE approvalStatus = :s", User.class)
                .setParameter("s", User.ApprovalStatus.PENDING).list();
        } catch (Exception e) {
            throw new RemoteException("Error fetching pending users: " + e.getMessage());
        }
    }

    @Override
    public void approveUser(Integer userId) throws RemoteException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user == null) throw new RemoteException("User not found.");
            user.setApprovalStatus(User.ApprovalStatus.APPROVED);
            user.setActive(true);
            session.merge(user);
            tx.commit();
            EmailService.sendApprovalNotification(user.getEmail(), user.getFullName() != null ? user.getFullName() : user.getEmail());
            notifier.publish("USER_APPROVED", "User approved: " + user.getEmail());
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error approving user: " + e.getMessage());
        }
    }

    @Override
    public void rejectUser(Integer userId) throws RemoteException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user == null) throw new RemoteException("User not found.");
            user.setApprovalStatus(User.ApprovalStatus.REJECTED);
            user.setActive(false);
            session.merge(user);
            tx.commit();
            EmailService.sendRejectionNotification(user.getEmail(), user.getFullName() != null ? user.getFullName() : user.getEmail());
            notifier.publish("USER_REJECTED", "User rejected: " + user.getEmail());
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error rejecting user: " + e.getMessage());
        }
    }

    
    
    @Override
    public User findUserById(Integer userId) throws RemoteException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, userId);
            if (user == null) throw new RemoteException("User not found.");
            return user;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error finding user: " + e.getMessage());
        }
    }
    
    @Override
    public List<User> findAllUsers() throws RemoteException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            throw new RemoteException("Error fetching all users: " + e.getMessage());
        }
    }
    
    @Override
    public User saveUser(User user) throws RemoteException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer userId = user.getId() != null ? user.getId() : 0;

            User existingEmail = session.createQuery("FROM User WHERE email = :email AND id != :id", User.class)
                .setParameter("email", user.getEmail())
                .setParameter("id", userId)
                .uniqueResult();
            if (existingEmail != null) {
                throw new RemoteException("Email is already registered.");
            }

            if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                User existingPhone = session.createQuery("FROM User WHERE phone = :phone AND id != :id", User.class)
                    .setParameter("phone", user.getPhone())
                    .setParameter("id", userId)
                    .uniqueResult();
                if (existingPhone != null) {
                    throw new RemoteException("Phone number is already registered.");
                }
            }

            Transaction tx = session.beginTransaction();
            if (user.getId() == null) {
                session.persist(user);
            } else {
                session.merge(user);
            }
            tx.commit();
            return user;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error saving user: " + e.getMessage());
        }
    }
    
    @Override
    public User updateUser(User user) throws RemoteException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Integer userId = user.getId() != null ? user.getId() : 0;

            User existingEmail = session.createQuery("FROM User WHERE email = :email AND id != :id", User.class)
                .setParameter("email", user.getEmail())
                .setParameter("id", userId)
                .uniqueResult();
            if (existingEmail != null) {
                throw new RemoteException("Email is already registered.");
            }

            if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
                User existingPhone = session.createQuery("FROM User WHERE phone = :phone AND id != :id", User.class)
                    .setParameter("phone", user.getPhone())
                    .setParameter("id", userId)
                    .uniqueResult();
                if (existingPhone != null) {
                    throw new RemoteException("Phone number is already registered.");
                }
            }

            Transaction tx = session.beginTransaction();
            User updatedUser = (User) session.merge(user);
            tx.commit();
            return updatedUser;
        } catch (RemoteException re) {
            throw re;
        } catch (Exception e) {
            throw new RemoteException("Error updating user: " + e.getMessage());
        }
    }
    
    @Override
    public void deleteUser(Integer userId) throws RemoteException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user == null) throw new RemoteException("User not found.");
            
            
            if (user.getProfile() != null) {
                session.remove(user.getProfile());
            }
            
            session.remove(user);
            tx.commit();
            notifier.publish("USER_DELETED", "User ID " + userId + " deleted.");
        } catch (Exception e) {
            throw new RemoteException("Error deleting user: " + e.getMessage());
        }
    }

    

    @Override
    public Farmer saveFarmer(Farmer farmer) throws RemoteException {
        try {
            ValidationUtil.validateFarmer(farmer);
            Farmer existing = farmerDAO.findByPhone(farmer.getPhone());
            if (existing != null) throw new RemoteException("Phone number is already registered.");
            Farmer saved = farmerDAO.save(farmer);
            notifier.publish("NEW_FARMER", "Farmer registered: " + farmer.getFullName());
            return saved;
        } catch (RemoteException re) { throw re; }
        catch (IllegalArgumentException e) { throw new RemoteException(e.getMessage()); }
        catch (Exception e) { throw new RemoteException("Error saving farmer: " + e.getMessage()); }
    }

    @Override
    public Farmer updateFarmer(Farmer farmer) throws RemoteException {
        try {
            ValidationUtil.validateFarmer(farmer);
            return farmerDAO.update(farmer);
        } catch (IllegalArgumentException e) { throw new RemoteException(e.getMessage()); }
        catch (Exception e) { throw new RemoteException("Error updating farmer: " + e.getMessage()); }
    }

    @Override
    public void deleteFarmer(Integer id) throws RemoteException {
        try { farmerDAO.delete(id); notifier.publish("FARMER_DELETED", "Farmer ID " + id + " deleted."); }
        catch (Exception e) { throw new RemoteException("Error deleting farmer: " + e.getMessage()); }
    }

    @Override public Farmer findFarmerById(Integer id) throws RemoteException { return farmerDAO.findById(id); }
    @Override public List<Farmer> findAllFarmers() throws RemoteException { return farmerDAO.findAll(); }
    @Override public List<Farmer> searchFarmersByLocation(String location) throws RemoteException { return farmerDAO.searchByLocation(location); }

    

    @Override
    public Harvest saveHarvest(Harvest harvest) throws RemoteException {
        try {
            ValidationUtil.validateHarvest(harvest);
            Harvest saved = harvestDAO.save(harvest);
            notifier.publish("HARVEST_LOGGED", "Harvest logged: " + harvest.getQuantityKg() + "kg");
            return saved;
        } catch (IllegalArgumentException e) { throw new RemoteException(e.getMessage()); }
        catch (Exception e) { throw new RemoteException("Error saving harvest: " + e.getMessage()); }
    }

    @Override public List<Harvest> findHarvestsByFarmerId(Integer farmerId) throws RemoteException { return harvestDAO.findByFarmerId(farmerId); }
    @Override public List<Harvest> findAllHarvests() throws RemoteException { return harvestDAO.findAll(); }

    

    @Override
    public Sale saveSale(Sale sale) throws RemoteException {
        try {
            ValidationUtil.validateSale(sale);
            double totalHarvest = harvestDAO.getTotalQuantityByFarmerAndCrop(sale.getFarmer().getId(), sale.getCropName());
            double totalSold    = saleDAO.getTotalSoldByFarmerAndCrop(sale.getFarmer().getId(), sale.getCropName());
            if (sale.getQuantitySold() > (totalHarvest - totalSold))
                throw new RemoteException("Insufficient stock. Available: " + (totalHarvest - totalSold) + "kg");
            sale.setTotalRevenue(sale.getQuantitySold() * sale.getUnitPrice());
            Sale saved = saleDAO.save(sale);
            notifier.publish("SALE_RECORDED", "Sale recorded: " + sale.getQuantitySold() + "kg");
            return saved;
        } catch (RemoteException re) { throw re; }
        catch (IllegalArgumentException e) { throw new RemoteException(e.getMessage()); }
        catch (Exception e) { throw new RemoteException("Error saving sale: " + e.getMessage()); }
    }

    @Override public List<Sale> findSalesByFarmerId(Integer farmerId) throws RemoteException { return saleDAO.findByFarmerId(farmerId); }
    @Override public List<Sale> findAllSales() throws RemoteException { return saleDAO.findAll(); }

    

    @Override
    public byte[] generateHarvestReportPDF(Integer farmerId) throws RemoteException {
        return com.wastonix.reporting.ReportGenerator.generateHarvestReportPDF(farmerId);
    }

    @Override
    public String exportHarvestsToCSV(Integer farmerId) throws RemoteException {
        return com.wastonix.reporting.ReportGenerator.exportHarvestsToCSV(farmerId);
    }
}
