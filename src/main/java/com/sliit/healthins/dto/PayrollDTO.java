package com.sliit.healthins.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayrollDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private BigDecimal amount;
    private LocalDate date;
    private String status;

    public PayrollDTO() {}

    // Getters
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }
}