package com.sliit.healthins.dto;

import com.sliit.healthins.model.Role;

public class UserDTO {
    private Long id;
    private String username;
    private String name;
    private String contact;
    private String email;
    private String phone;
    private String status;
    private String password;
    private Role role;
    private boolean isActive;
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private String branch;

    // Default constructor for ModelMapper
    public UserDTO() {
    }

    public UserDTO(Long id, String username, String name, String contact, String email, String phone, boolean isActive, Role role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.phone = phone;
        this.isActive = isActive;
        this.status = isActive ? "Active" : "Inactive";
        this.role = role;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public boolean isActive() { return isActive; }
    public String getBankName() { return bankName; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public String getBranch() { return branch; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setStatus(String status) { this.status = status; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
    public void setActive(boolean active) { isActive = active; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    public void setBranch(String branch) { this.branch = branch; }
}