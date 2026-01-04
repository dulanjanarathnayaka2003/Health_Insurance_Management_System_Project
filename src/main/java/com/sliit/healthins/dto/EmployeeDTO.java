package com.sliit.healthins.dto;

import java.time.LocalDate;

public class EmployeeDTO {
    private Long id;
    private Long userId;
    private String name;  // User's name
    private String userRole;  // User's role (CUSTOMER, etc.)
    private String department;
    private Double salary;
    private LocalDate hireDate;
    private Integer performanceRating;

    // Default constructor
    public EmployeeDTO() {
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getUserRole() { return userRole; }
    public String getDepartment() { return department; }
    public Double getSalary() { return salary; }
    public LocalDate getHireDate() { return hireDate; }
    public Integer getPerformanceRating() { return performanceRating; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public void setDepartment(String department) { this.department = department; }
    public void setSalary(Double salary) { this.salary = salary; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public void setPerformanceRating(Integer performanceRating) { this.performanceRating = performanceRating; }
}