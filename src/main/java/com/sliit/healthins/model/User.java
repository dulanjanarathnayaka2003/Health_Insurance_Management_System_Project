package com.sliit.healthins.model;

import com.sliit.healthins.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Hash in service layer

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("customer")
    private List<Policy> policies;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("customer")
    private List<Inquiry> inquiries;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", referencedColumnName = "id")
    @JsonIgnoreProperties("user")
    private BankAccount bankAccount;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    public User(String username, String password, String name, String contact, String email, String phone, boolean isActive, Role role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.phone = phone;
        this.isActive = isActive;
        this.role = role;
    }

    public User(UserDTO userDTO) {
        this.username = userDTO.getUsername();
        this.password = userDTO.getPassword();
        this.name = userDTO.getName();
        this.contact = userDTO.getContact();
        this.email = userDTO.getEmail();
        this.phone = userDTO.getPhone();
        this.isActive = userDTO.isActive();
        this.role = Role.valueOf(userDTO.getRole().name()); // Assume UserDTO has Role enum
    }

    public UserDTO toDTO() {
        return new UserDTO(this.id, this.username, this.name, this.contact, this.email, this.phone, this.isActive, this.role);
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isActive() { return isActive; }
    public Role getRole() { return role; }
    public List<Policy> getPolicies() { return policies; }
    public List<Inquiry> getInquiries() { return inquiries; }
    public BankAccount getBankAccount() { return bankAccount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setActive(boolean active) { isActive = active; }
    public void setRole(Role role) { this.role = role; }
    public void setPolicies(List<Policy> policies) { this.policies = policies; }
    public void setInquiries(List<Inquiry> inquiries) { this.inquiries = inquiries; }
    public void setBankAccount(BankAccount bankAccount) { this.bankAccount = bankAccount; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}