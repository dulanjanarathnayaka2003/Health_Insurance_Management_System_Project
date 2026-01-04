package com.sliit.healthins.controller;

import com.sliit.healthins.model.User;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.repository.PolicyRepository;
import com.sliit.healthins.repository.ClaimRepository;
import com.sliit.healthins.repository.PaymentRepository;
import com.sliit.healthins.repository.EmployeeRepository;
import com.sliit.healthins.repository.InquiryRepository;
import com.sliit.healthins.repository.CampaignRepository;
import com.sliit.healthins.repository.PayrollRepository;
import com.sliit.healthins.repository.PerformanceReviewRepository;
import com.sliit.healthins.repository.AuditLogRepository;
import com.sliit.healthins.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PolicyRepository policyRepository;
    
    @Autowired
    private ClaimRepository claimRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private InquiryRepository inquiryRepository;
    
    @Autowired
    private CampaignRepository campaignRepository;
    
    @Autowired
    private PayrollRepository payrollRepository;
    
    @Autowired
    private PerformanceReviewRepository performanceReviewRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role userRole = user.getRole();
        
        // Add user info to model
        model.addAttribute("user", user);
        model.addAttribute("userRole", userRole);
        
        // Add role-specific data based on user role
        switch (userRole) {
            case ADMIN:
                return adminDashboard(model);
            case CUSTOMER:
            case POLICYHOLDER:
                return customerDashboard(model, user);
            case HR:
                return hrDashboard(model);
            case MARKETING:
                return marketingDashboard(model);
            case CLAIMS_PROCESSING:
                return claimsDashboard(model);
            case CUSTOMER_SERVICE:
                return customerSupportDashboard(model);
            default:
                return "redirect:/login.html";
        }
    }
    
    private String adminDashboard(Model model) {
        // Admin-specific data
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalPolicies", policyRepository.count());
        model.addAttribute("totalClaims", claimRepository.count());
        model.addAttribute("totalPayments", paymentRepository.count());
        model.addAttribute("totalEmployees", employeeRepository.count());
        model.addAttribute("totalInquiries", inquiryRepository.count());
        model.addAttribute("totalCampaigns", campaignRepository.count());
        model.addAttribute("totalPayrolls", payrollRepository.count());
        model.addAttribute("totalAuditLogs", auditLogRepository.count());
        
        // Recent activities
        model.addAttribute("recentUsers", userRepository.findAll().stream().limit(5).toList());
        model.addAttribute("recentPolicies", policyRepository.findAll().stream().limit(5).toList());
        model.addAttribute("recentClaims", claimRepository.findAll().stream().limit(5).toList());
        model.addAttribute("recentAuditLogs", auditLogRepository.findAll().stream().limit(10).toList());
        
        return "redirect:/admin-system-settings.html";
    }
    
    private String customerDashboard(Model model, User user) {
        // Customer-specific data
        model.addAttribute("userPolicies", policyRepository.findByCustomerId(user.getId()));
        model.addAttribute("userClaims", claimRepository.findByPolicyCustomerId(user.getId()));
        model.addAttribute("userInquiries", inquiryRepository.findByCustomerId(user.getId()));
        
        // Calculate totals
        model.addAttribute("totalPolicies", policyRepository.findByCustomerId(user.getId()).size());
        model.addAttribute("totalClaims", claimRepository.findByPolicyCustomerId(user.getId()).size());
        model.addAttribute("totalInquiries", inquiryRepository.findByCustomerId(user.getId()).size());
        
        return "redirect:/customer-portal.html";
    }
    
    private String hrDashboard(Model model) {
        // HR-specific data
        model.addAttribute("totalEmployees", employeeRepository.count());
        model.addAttribute("totalPayrolls", payrollRepository.count());
        model.addAttribute("totalPerformanceReviews", performanceReviewRepository.count());
        
        // Employee data
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("payrolls", payrollRepository.findAll());
        model.addAttribute("performanceReviews", performanceReviewRepository.findAll());
        
        // Recent activities
        model.addAttribute("recentPayrolls", payrollRepository.findAll().stream().limit(5).toList());
        model.addAttribute("recentPerformanceReviews", performanceReviewRepository.findAll().stream().limit(5).toList());
        
        return "redirect:/hr-manager.html";
    }
    
    private String marketingDashboard(Model model) {
        // Marketing-specific data
        model.addAttribute("totalCampaigns", campaignRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        
        // Campaign data
        model.addAttribute("campaigns", campaignRepository.findAll());
        model.addAttribute("activeCampaigns", campaignRepository.findByStatus(com.sliit.healthins.model.CampaignStatus.ACTIVE));
        model.addAttribute("completedCampaigns", campaignRepository.findByStatus(com.sliit.healthins.model.CampaignStatus.COMPLETED));
        
        // Customer segments
        model.addAttribute("totalCustomers", userRepository.countByRole(Role.CUSTOMER));
        model.addAttribute("totalPolicyholders", userRepository.countByRole(Role.POLICYHOLDER));
        
        return "redirect:/marketing-manager.html";
    }
    
    private String claimsDashboard(Model model) {
        // Claims processing data
        model.addAttribute("totalClaims", claimRepository.count());
        model.addAttribute("pendingClaims", claimRepository.findByStatus(com.sliit.healthins.model.ClaimStatus.PENDING));
        model.addAttribute("approvedClaims", claimRepository.findByStatus(com.sliit.healthins.model.ClaimStatus.APPROVED));
        model.addAttribute("rejectedClaims", claimRepository.findByStatus(com.sliit.healthins.model.ClaimStatus.REJECTED));
        model.addAttribute("underReviewClaims", claimRepository.findByStatus(com.sliit.healthins.model.ClaimStatus.UNDER_REVIEW));
        
        // Recent claims
        model.addAttribute("recentClaims", claimRepository.findAll().stream().limit(10).toList());
        
        return "redirect:/claims-processing.html";
    }
    
    private String customerSupportDashboard(Model model) {
        // Customer support data
        model.addAttribute("totalInquiries", inquiryRepository.count());
        model.addAttribute("openInquiries", inquiryRepository.findByStatus(com.sliit.healthins.model.InquiryStatus.OPEN));
        model.addAttribute("resolvedInquiries", inquiryRepository.findByStatus(com.sliit.healthins.model.InquiryStatus.RESOLVED));
        
        // Recent inquiries
        model.addAttribute("recentInquiries", inquiryRepository.findAll().stream().limit(10).toList());
        
        // Customer data
        model.addAttribute("totalCustomers", userRepository.countByRole(Role.CUSTOMER));
        model.addAttribute("totalPolicyholders", userRepository.countByRole(Role.POLICYHOLDER));
        
        return "redirect:/customer_support.html";
    }
}
