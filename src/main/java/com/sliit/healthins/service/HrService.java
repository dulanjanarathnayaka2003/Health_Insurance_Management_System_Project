package com.sliit.healthins.service;

import com.sliit.healthins.dto.EmployeeDTO;
import com.sliit.healthins.dto.PayrollDTO;
import com.sliit.healthins.dto.ReviewDTO;
import com.sliit.healthins.dto.UserDTO;
import com.sliit.healthins.model.Employee;
import com.sliit.healthins.model.Payroll;
import com.sliit.healthins.model.PayrollStatus;
import com.sliit.healthins.model.PerformanceReview;
import com.sliit.healthins.model.Role;
import com.sliit.healthins.model.User;
import com.sliit.healthins.repository.EmployeeRepository;
import com.sliit.healthins.repository.PayrollRepository;
import com.sliit.healthins.repository.PerformanceReviewRepository;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.util.PdfGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Transactional(readOnly = true)
public class HrService {
    private final EmployeeRepository employeeRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final PayrollRepository payrollRepository;
    private final PdfGeneratorUtil pdfGeneratorUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public HrService(EmployeeRepository employeeRepository,
                     PerformanceReviewRepository performanceReviewRepository,
                     PayrollRepository payrollRepository,
                     PdfGeneratorUtil pdfGeneratorUtil,
                     UserRepository userRepository,
                     PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.performanceReviewRepository = performanceReviewRepository;
        this.payrollRepository = payrollRepository;
        this.pdfGeneratorUtil = pdfGeneratorUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Employee createEmployee(EmployeeDTO dto) {
        if (dto == null || dto.getUserId() == null) {
            throw new IllegalArgumentException("EmployeeDTO or userId cannot be null");
        }
        if (dto.getPerformanceRating() != null && dto.getPerformanceRating() > 5) {
            throw new IllegalArgumentException("Performance rating must be between 0 and 5");
        }
        Employee employee = new Employee();
        employee.setUserId(dto.getUserId());
        employee.setDepartment(dto.getDepartment());
        employee.setSalary(dto.getSalary());
        employee.setHireDate(dto.getHireDate());
        employee.setPerformanceRating(dto.getPerformanceRating());
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Long id, EmployeeDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
        if (dto.getUserId() != null) employee.setUserId(dto.getUserId());
        if (dto.getDepartment() != null) employee.setDepartment(dto.getDepartment());
        if (dto.getSalary() != null) employee.setSalary(dto.getSalary());
        if (dto.getHireDate() != null) employee.setHireDate(dto.getHireDate());
        if (dto.getPerformanceRating() != null && dto.getPerformanceRating() <= 5) {
            employee.setPerformanceRating(dto.getPerformanceRating());
        }
        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }


    @Transactional
    public PerformanceReview addReview(ReviewDTO dto) {
        if (dto == null || dto.getEmployeeId() == null) {
            throw new IllegalArgumentException("ReviewDTO or employeeId cannot be null");
        }

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + dto.getEmployeeId()));

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(employee);
        review.setRating(dto.getRating());
        review.setComments(dto.getComments());
        review.setDate(dto.getDate());

        return performanceReviewRepository.save(review);
    }

    public byte[] generateHrReport(LocalDate start, LocalDate end, String type) throws IOException {
        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        
        StringBuilder content = new StringBuilder();
        content.append("HR Report\n");
        content.append("Period: ").append(start).append(" to ").append(end).append("\n");
        content.append("Report Type: ").append(type).append("\n\n");
        
        // Get customers and payroll data
        List<User> allCustomers = userRepository.findByRole(Role.CUSTOMER);
        List<Payroll> allPayrolls = payrollRepository.findAll();
        
        // Filter by date range
        List<User> customersInRange = allCustomers.stream()
                .filter(c -> c.getCreatedAt() != null && 
                            !c.getCreatedAt().toLocalDate().isBefore(start) && 
                            !c.getCreatedAt().toLocalDate().isAfter(end))
                .collect(Collectors.toList());
        
        List<Payroll> payrollsInRange = allPayrolls.stream()
                .filter(p -> p.getDate() != null && 
                            !p.getDate().isBefore(start) && 
                            !p.getDate().isAfter(end))
                .collect(Collectors.toList());
        
        // Add customer statistics
        if (!"PAYROLL".equals(type)) {
            content.append("CUSTOMER STATISTICS\n");
            content.append("==================\n");
            content.append("Total Customers: ").append(allCustomers.size()).append("\n");
            content.append("New Customers in Period: ").append(customersInRange.size()).append("\n\n");
            
            if ("DETAILED".equals(type) || "CUSTOMERS".equals(type)) {
                content.append("Customer Details:\n");
                customersInRange.forEach(c -> {
                    content.append("  - ID: ").append(c.getId())
                           .append(", Name: ").append(c.getName())
                           .append(", Email: ").append(c.getEmail())
                           .append(", Created: ").append(c.getCreatedAt().toLocalDate())
                           .append("\n");
                });
                content.append("\n");
            }
        }
        
        // Add payroll statistics
        if (!"CUSTOMERS".equals(type)) {
            content.append("PAYROLL STATISTICS\n");
            content.append("==================\n");
            long paidCount = payrollsInRange.stream().filter(p -> p.getStatus() == PayrollStatus.PAID).count();
            long pendingCount = payrollsInRange.stream().filter(p -> p.getStatus() == PayrollStatus.PENDING).count();
            double totalAmount = payrollsInRange.stream().mapToDouble(p -> p.getAmount().doubleValue()).sum();
            
            content.append("Total Payrolls in Period: ").append(payrollsInRange.size()).append("\n");
            content.append("Paid: ").append(paidCount).append("\n");
            content.append("Pending: ").append(pendingCount).append("\n");
            content.append("Total Amount: $").append(String.format("%.2f", totalAmount)).append("\n\n");
            
            if ("DETAILED".equals(type) || "PAYROLL".equals(type)) {
                content.append("Payroll Details:\n");
                payrollsInRange.forEach(p -> {
                    String employeeInfo = p.getEmployee() != null ? 
                        String.valueOf(p.getEmployee().getId()) : "N/A";
                    content.append("  - ID: ").append(p.getId())
                           .append(", Employee ID: ").append(employeeInfo)
                           .append(", Amount: $").append(String.format("%.2f", p.getAmount().doubleValue()))
                           .append(", Date: ").append(p.getDate())
                           .append(", Status: ").append(p.getStatus())
                           .append("\n");
                });
                content.append("\n");
            }
        }
        
        content.append("\nReport Generated: ").append(LocalDate.now()).append("\n");
        
        return pdfGeneratorUtil.generatePdf("HR Report - " + type, content.toString());
    }

    @Transactional
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public List<UserDTO> getCustomers() {
        return userRepository.findByRole(Role.CUSTOMER).stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO createCustomer(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setName(dto.getName() != null ? dto.getName() : dto.getUsername());
        user.setContact(dto.getContact() != null ? dto.getContact() : dto.getPhone());
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        
        User savedUser = userRepository.save(user);
        return convertToUserDTO(savedUser);
    }

    @Transactional
    public UserDTO updateCustomer(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));
        
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getContact() != null) user.setContact(dto.getContact());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return convertToUserDTO(updatedUser);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + id));
        userRepository.delete(user);
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setContact(user.getContact());
        dto.setActive(user.isActive());
        dto.setRole(user.getRole());
        dto.setStatus(user.isActive() ? "Active" : "Inactive");
        return dto;
    }

    /**
     * Get employee by Employee ID
     */
    public EmployeeDTO getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
        return convertToEmployeeDTO(employee);
    }
    
    /**
     * Get all internal staff (non-customers) with their employee information
     */
    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllInternalStaff() {
        // Get all users who are NOT customers
        List<User> internalStaff = userRepository.findAll().stream()
                .filter(user -> user.getRole() != Role.CUSTOMER && user.getRole() != Role.POLICYHOLDER)
                .collect(Collectors.toList());
        
        // Convert to DTOs with employee information
        return internalStaff.stream().map(user -> {
            EmployeeDTO dto = new EmployeeDTO();
            dto.setUserId(user.getId());
            dto.setName(user.getName());
            dto.setUserRole(user.getRole().name());
            
            // Check if employee record exists
            Employee employee = employeeRepository.findByUser_Id(user.getId()).orElse(null);
            if (employee != null) {
                dto.setId(employee.getId());
                dto.setDepartment(employee.getDepartment());
                dto.setSalary(employee.getSalary());
                dto.setHireDate(employee.getHireDate());
                dto.setPerformanceRating(employee.getPerformanceRating());
            } else {
                // No employee record yet
                dto.setDepartment("Not Set");
                dto.setSalary(0.0);
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * Search for employee by USER ID (not Employee ID)
     * Only works for internal staff, NOT customers
     * If user exists but has no employee record, creates one automatically
     */
    @Transactional
    public EmployeeDTO searchEmployeeByUserId(Long userId) {
        System.out.println("🔍 Searching for user with ID: " + userId);
        
        // First, check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        System.out.println("✅ Found user: " + user.getUsername() + " (Role: " + user.getRole() + ")");
        
        // REJECT if user is a CUSTOMER or POLICYHOLDER
        if (user.getRole() == Role.CUSTOMER || user.getRole() == Role.POLICYHOLDER) {
            throw new IllegalArgumentException("Cannot process payroll for customers. Payroll is only for internal employees (Admin, HR, Marketing, Claims Processing, Customer Service).");
        }
        
        // Check if this user already has an employee record
        Employee employee = employeeRepository.findByUser_Id(userId).orElse(null);
        
        if (employee == null) {
            // Auto-create employee record with default values for internal staff only
            System.out.println("📝 Creating new employee record for user: " + user.getUsername());
            employee = new Employee();
            employee.setUser(user);  // Set the actual User object
            employee.setDepartment(getDepartmentByRole(user.getRole()));
            employee.setSalary(0.0); // Default salary - HR can update later
            employee.setHireDate(LocalDate.now());
            employee.setPerformanceRating(3); // Default rating
            employee = employeeRepository.save(employee);
            System.out.println("✅ Created employee record with ID: " + employee.getId());
        } else {
            System.out.println("✅ Found existing employee record with ID: " + employee.getId());
        }
        
        // Convert to DTO and include user information
        EmployeeDTO dto = convertToEmployeeDTO(employee);
        dto.setName(user.getName());
        dto.setUserRole(user.getRole().name());
        
        return dto;
    }
    
    /**
     * Helper method to determine department based on role
     */
    private String getDepartmentByRole(Role role) {
        switch (role) {
            case ADMIN:
                return "IT / Administration";
            case HR:
                return "Human Resources";
            case MARKETING:
                return "Marketing";
            case CLAIMS_PROCESSING:
                return "Claims Processing";
            case CUSTOMER_SERVICE:
                return "Customer Service";
            default:
                return "General";
        }
    }

    public List<PayrollDTO> getAllPayrolls() {
        return payrollRepository.findAll().stream()
                .map(this::convertToPayrollDTO)
                .collect(Collectors.toList());
    }

    public List<PayrollDTO> searchPayrollsByEmployee(Long employeeId) {
        return payrollRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToPayrollDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PayrollDTO updatePayrollStatus(Long id, String status) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll not found with id: " + id));
        payroll.setStatus(PayrollStatus.valueOf(status));
        Payroll updatedPayroll = payrollRepository.save(payroll);
        return convertToPayrollDTO(updatedPayroll);
    }

    @Transactional
    public PayrollDTO processPayroll(PayrollDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + dto.getEmployeeId()));

        Payroll payroll = new Payroll();
        payroll.setEmployee(employee);
        payroll.setAmount(dto.getAmount());
        payroll.setDate(dto.getDate());
        payroll.setStatus(PayrollStatus.valueOf(dto.getStatus()));

        Payroll savedPayroll = payrollRepository.save(payroll);
        return convertToPayrollDTO(savedPayroll);
    }

    private EmployeeDTO convertToEmployeeDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setUserId(employee.getUserId() != null ? ((Long) employee.getUserId()) : null);
        dto.setDepartment(employee.getDepartment());
        dto.setSalary(employee.getSalary());
        dto.setHireDate(employee.getHireDate());
        dto.setPerformanceRating(employee.getPerformanceRating());
        return dto;
    }

    private PayrollDTO convertToPayrollDTO(Payroll payroll) {
        PayrollDTO dto = new PayrollDTO();
        dto.setId(payroll.getId());
        dto.setEmployeeId(payroll.getEmployee().getId());
        dto.setEmployeeName(payroll.getEmployee().getUser() != null ? payroll.getEmployee().getUser().getName() : "Unknown");
        dto.setAmount(payroll.getAmount());
        dto.setDate(payroll.getDate());
        dto.setStatus(payroll.getStatus().name());
        return dto;
    }
}