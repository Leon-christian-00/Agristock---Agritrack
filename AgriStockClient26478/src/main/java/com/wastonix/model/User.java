package com.wastonix.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ApprovalStatus { PENDING, APPROVED, REJECTED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phone;

    private String fullName;

    private String otpCode;
    private LocalDateTime otpExpiresAt;
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;

    public Integer getId() { return id; } public void setId(Integer id) { this.id = id; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getFullName() { return fullName; } public void setFullName(String fullName) { this.fullName = fullName; }
    public String getOtpCode() { return otpCode; } public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    public LocalDateTime getOtpExpiresAt() { return otpExpiresAt; } public void setOtpExpiresAt(LocalDateTime otpExpiresAt) { this.otpExpiresAt = otpExpiresAt; }
    public boolean isActive() { return isActive; } public void setActive(boolean active) { isActive = active; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; } public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public UserProfile getProfile() { return profile; } public void setProfile(UserProfile profile) { this.profile = profile; }
}
