package com.sliit.healthins.service;

import com.sliit.healthins.dto.*;
import com.sliit.healthins.model.BankAccount;
import com.sliit.healthins.model.Role;
import com.sliit.healthins.model.User;
import com.sliit.healthins.repository.BankAccountRepository;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.util.PdfGeneratorUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final PdfGeneratorUtil pdfGeneratorUtil;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(UserRepository userRepository, BankAccountRepository bankAccountRepository, 
                       PdfGeneratorUtil pdfGeneratorUtil, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.pdfGeneratorUtil = pdfGeneratorUtil;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user based on the provided DTO.
     *
     * @param dto The user DTO containing details
     * @return The created user as a DTO
     * @throws IllegalArgumentException if DTO or username is null/empty
     */
    @Transactional
    public UserDTO createUser(UserDTO dto) {
        System.out.println("🔵 Creating user: " + dto.getUsername() + ", Role: " + dto.getRole());
        
        if (dto == null || dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("UserDTO or username cannot be null or empty");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + dto.getUsername());
        }
        
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "defaultPassword"));
        user.setName(dto.getName() != null ? dto.getName() : dto.getUsername());
        user.setContact(dto.getContact() != null ? dto.getContact() : dto.getEmail());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setActive(dto.isActive());
        
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        } else {
            user.setRole(Role.USER); // Default role
        }
        
        // Create bank account if user is CUSTOMER and bank details are provided
        if (dto.getRole() == Role.CUSTOMER && dto.getBankName() != null && dto.getAccountNumber() != null) {
            System.out.println("🏦 Creating bank account for customer: " + dto.getBankName() + " - " + dto.getAccountNumber());
            
            // Check if account number already exists
            if (bankAccountRepository.existsByAccountNumber(dto.getAccountNumber())) {
                throw new IllegalArgumentException("Bank account number already exists");
            }
            
            BankAccount bankAccount = new BankAccount(
                dto.getBankName(),
                dto.getAccountNumber(),
                dto.getAccountHolderName() != null ? dto.getAccountHolderName() : dto.getName(),
                dto.getBranch()
            );
            BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);
            System.out.println("✅ Bank account saved with ID: " + savedBankAccount.getId());
            user.setBankAccount(savedBankAccount);
        }
        
        User savedUser = userRepository.save(user);
        System.out.println("✅ User saved successfully with ID: " + savedUser.getId());
        return convertToDTO(savedUser);
    }

    /**
     * Updates an existing user based on the provided DTO.
     *
     * @param id  The ID of the user to update
     * @param dto The user DTO with updated details
     * @return The updated user as a DTO
     * @throws IllegalArgumentException if user not found or DTO is invalid
     */
    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty() && !user.getUsername().equals(dto.getUsername())) {
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new IllegalArgumentException("Username already exists: " + dto.getUsername());
            }
            user.setUsername(dto.getUsername());
        }
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getContact() != null) user.setContact(dto.getContact());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getStatus() != null) user.setActive("Active".equalsIgnoreCase(dto.getStatus()));
        if (dto.getRole() != null) user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id The ID of the user to delete
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Activates all existing customers and policyholders in the database.
     * This is useful for migrating existing customers who may have isActive = false.
     *
     * @return Number of users activated
     */
    @Transactional
    public int activateAllCustomers() {
        List<User> customers = userRepository.findByRole(Role.CUSTOMER);
        List<User> policyholders = userRepository.findByRole(Role.POLICYHOLDER);
        
        int activatedCount = 0;
        for (User user : customers) {
            if (!user.isActive()) {
                user.setActive(true);
                userRepository.save(user);
                activatedCount++;
            }
        }
        for (User user : policyholders) {
            if (!user.isActive()) {
                user.setActive(true);
                userRepository.save(user);
                activatedCount++;
            }
        }
        
        System.out.println("✅ Activated " + activatedCount + " customers/policyholders");
        return activatedCount;
    }

    /**
     * Retrieves system metrics for monitoring.
     *
     * @return SystemMetricsDTO containing system metrics
     */
    public SystemMetricsDTO getSystemMetrics() {
        try {
            // Get basic metrics
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.countByIsActiveTrue();
            
            // Calculate uptime (simplified - in production, you'd track actual start time)
            long uptimeSeconds = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
            String uptime = formatUptime(uptimeSeconds);
            
            // Get memory usage
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            double memoryUsage = (double) usedMemory / totalMemory * 100;
            
            // Get system info
            String serverInfo = System.getProperty("java.version") + " - " + 
                               System.getProperty("os.name") + " " + 
                               System.getProperty("os.version");
            
            // Database status (simplified check)
            String databaseStatus = "Connected";
            try {
                userRepository.count(); // Simple query to test connection
            } catch (Exception e) {
                databaseStatus = "Disconnected";
            }
            
            return new SystemMetricsDTO(
                "Online",
                uptime,
                1, // Active connections (simplified)
                (int) totalUsers,
                (int) activeUsers,
                LocalDateTime.now(),
                serverInfo,
                databaseStatus,
                memoryUsage,
                0.0 // CPU usage (would need additional libraries for accurate measurement)
            );
        } catch (Exception e) {
            return new SystemMetricsDTO(
                "Error",
                "Unknown",
                0,
                0,
                0,
                LocalDateTime.now(),
                "Error retrieving info",
                "Error",
                0.0,
                0.0
            );
        }
    }
    
    private String formatUptime(long seconds) {
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, secs);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }

    /**
     * Updates a system configuration setting.
     *
     * @param dto The config DTO containing key-value pair
     * @return The updated config DTO
     * @throws IllegalArgumentException if DTO or key is null/empty
     */
    @Transactional
    public ConfigDTO updateConfig(ConfigDTO dto) {
        if (dto == null || dto.getKey() == null || dto.getKey().trim().isEmpty()) {
            throw new IllegalArgumentException("ConfigDTO or key cannot be null or empty");
        }
        // In a real implementation, integrate with SystemConfigRepository
        // e.g., systemConfigRepository.findByKey(dto.getKey()).ifPresentOrElse(
        //     config -> { config.setValue(dto.getValue()); systemConfigRepository.save(config); },
        //     () -> { systemConfigRepository.save(new SystemConfig(dto.getKey(), dto.getValue())); }
        // );
        return dto; // Placeholder; assume in-memory or external config service
    }

    /**
     * Gets the current system configuration.
     *
     * @return The current config DTO
     */
    public ConfigDTO getConfig() {
        // In a real implementation, load from database or config service
        ConfigDTO config = new ConfigDTO();
        config.setKey("system");
        config.setValue("active");
        return config;
    }

    /**
     * Saves system configuration settings.
     *
     * @param dto The system config DTO
     * @return The saved system config DTO
     */
    @Transactional
    public SystemConfigDTO saveSystemConfig(SystemConfigDTO dto) {
        // In a real implementation, save to database
        System.out.println("💾 Saving system config: " + dto.getSiteName() + ", " + dto.getTimezone());
        return dto;
    }

    /**
     * Saves security configuration settings.
     *
     * @param dto The security config DTO
     * @return The saved security config DTO
     */
    @Transactional
    public SecurityConfigDTO saveSecurityConfig(SecurityConfigDTO dto) {
        // In a real implementation, save to database
        System.out.println("🔒 Saving security config: Session timeout=" + dto.getSessionTimeout() + ", Max attempts=" + dto.getMaxLoginAttempts());
        return dto;
    }

    /**
     * Saves email configuration settings.
     *
     * @param dto The email config DTO
     * @return The saved email config DTO
     */
    @Transactional
    public EmailConfigDTO saveEmailConfig(EmailConfigDTO dto) {
        // In a real implementation, save to database
        System.out.println("📧 Saving email config: " + dto.getSmtpServer() + ":" + dto.getSmtpPort());
        return dto;
    }

    /**
     * Saves API configuration settings.
     *
     * @param dto The API config DTO
     * @return The saved API config DTO
     */
    @Transactional
    public ApiConfigDTO saveApiConfig(ApiConfigDTO dto) {
        // In a real implementation, save to database
        System.out.println("🔌 Saving API config: Rate limit=" + dto.getApiRateLimit() + ", Timeout=" + dto.getApiTimeout());
        return dto;
    }

    /**
     * Saves payment gateway configuration settings.
     *
     * @param dto The payment config DTO
     * @return The saved payment config DTO
     */
    @Transactional
    public PaymentConfigDTO savePaymentConfig(PaymentConfigDTO dto) {
        // In a real implementation, save to database
        System.out.println("💳 Saving payment config: Gateway=" + dto.getPaymentGateway() + ", Test mode=" + dto.isTestMode());
        return dto;
    }

    /**
     * Generates an audit log report for the given date range.
     *
     * @param start The start datetime of the audit period
     * @param end   The end datetime of the audit period
     * @return Byte array of the generated PDF audit log
     * @throws IllegalArgumentException if dates are invalid
     */
    public byte[] generateAuditLog(LocalDateTime start, LocalDateTime end, String type) throws IOException {
        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid date range");
        }
        
        StringBuilder content = new StringBuilder();
        content.append("SYSTEM AUDIT LOG\n");
        content.append("================\n\n");
        content.append("Period: ").append(start).append(" to ").append(end).append("\n");
        content.append("Report Type: ").append(type).append("\n");
        content.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
        
        // Get all users
        java.util.List<User> allUsers = userRepository.findAll();
        
        // Filter users created in the date range
        java.util.List<User> usersInRange = allUsers.stream()
                .filter(u -> {
                    LocalDateTime createdAt = u.getCreatedAt();
                    return createdAt != null && 
                           (createdAt.isAfter(start) || createdAt.isEqual(start)) && 
                           (createdAt.isBefore(end) || createdAt.isEqual(end));
                })
                .collect(java.util.stream.Collectors.toList());
        
        // Generate report based on type
        if ("ALL".equals(type) || "USER_MANAGEMENT".equals(type)) {
            content.append("USER MANAGEMENT ACTIVITIES\n");
            content.append("==========================\n");
            content.append("Total Users Created: ").append(usersInRange.size()).append("\n");
            content.append("Total Active Users: ").append(
                allUsers.stream().filter(User::isActive).count()
            ).append("\n");
            content.append("Total Inactive Users: ").append(
                allUsers.stream().filter(u -> !u.isActive()).count()
            ).append("\n\n");
            
            // User breakdown by role
            content.append("Users by Role:\n");
            for (Role role : Role.values()) {
                long count = allUsers.stream().filter(u -> u.getRole() == role).count();
                if (count > 0) {
                    content.append("  - ").append(role).append(": ").append(count).append("\n");
                }
            }
            content.append("\n");
            
            // Detailed user activities in period
            if ("USER_MANAGEMENT".equals(type) && !usersInRange.isEmpty()) {
                content.append("New Users Created in Period:\n");
                usersInRange.forEach(u -> {
                    content.append("  - ID: ").append(u.getId())
                           .append(", Username: ").append(u.getUsername())
                           .append(", Email: ").append(u.getEmail())
                           .append(", Role: ").append(u.getRole())
                           .append(", Status: ").append(u.isActive() ? "Active" : "Inactive")
                           .append(", Created: ").append(u.getCreatedAt())
                           .append("\n");
                });
                content.append("\n");
            }
        }
        
        if ("ALL".equals(type) || "SYSTEM".equals(type)) {
            content.append("SYSTEM EVENTS\n");
            content.append("=============\n");
            content.append("Database Status: Connected\n");
            content.append("Total Database Records: ").append(userRepository.count()).append("\n");
            content.append("System Health: Operational\n");
            content.append("Last System Restart: N/A\n\n");
        }
        
        if ("ALL".equals(type) || "CLAIMS".equals(type)) {
            content.append("CLAIMS PROCESSING\n");
            content.append("=================\n");
            content.append("Note: Claims data available through Claims Processing module\n");
            content.append("This section tracks claim submissions, approvals, and rejections\n\n");
        }
        
        if ("ALL".equals(type) || "PAYMENTS".equals(type)) {
            content.append("PAYMENT ACTIVITIES\n");
            content.append("==================\n");
            content.append("Note: Payment data available through Customer Portal module\n");
            content.append("This section tracks payment transactions and status updates\n\n");
        }
        
        content.append("\n=== END OF AUDIT LOG ===\n");
        content.append("Report generated by: System Administrator\n");
        content.append("This is an official system audit document.\n");
        
        return pdfGeneratorUtil.generatePdf("System Audit Log - " + type, content.toString());
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return List of all users as DTOs
     */
    @Transactional(readOnly = true)
    public java.util.List<UserDTO> getAllUsers() {
        System.out.println("📊 Getting all users from database...");
        java.util.List<User> allUsers = userRepository.findAll();
        
        // Force initialization of lazy-loaded bank accounts within transaction
        allUsers.forEach(user -> {
            if (user.getBankAccount() != null) {
                user.getBankAccount().getBankName(); // Force initialization
            }
        });
        
        java.util.List<UserDTO> users = allUsers.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
        System.out.println("✅ Returning " + users.size() + " users");
        return users;
    }

    /**
     * Converts User entity to UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setContact(user.getContact());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setActive(user.isActive());
        dto.setStatus(user.isActive() ? "Active" : "Inactive");
        dto.setRole(user.getRole());
        
        // Include bank account details if available (check for null safely)
        try {
            if (user.getBankAccount() != null) {
                BankAccount bankAccount = user.getBankAccount();
                dto.setBankName(bankAccount.getBankName());
                dto.setAccountNumber(bankAccount.getAccountNumber());
                dto.setAccountHolderName(bankAccount.getAccountHolderName());
                dto.setBranch(bankAccount.getBranch());
            }
        } catch (Exception e) {
            // Bank account not loaded or error accessing it - skip bank details
            System.out.println("⚠️ Could not load bank account for user: " + user.getUsername());
        }
        
        return dto;
    }
}