package com.wastonix.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_profiles")
public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String fullName;
    @Column(unique = true)
    private String phone;
    private String role = "OFFICER";

    
    public Integer getUserId() { return userId; } public void setUserId(Integer userId) { this.userId = userId; }
    public User getUser() { return user; } public void setUser(User user) { this.user = user; }
    public String getFullName() { return fullName; } public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; } public void setRole(String role) { this.role = role; }
}