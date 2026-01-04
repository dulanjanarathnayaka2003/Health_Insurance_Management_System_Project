package com.sliit.healthins.config;

import com.sliit.healthins.model.*;
import com.sliit.healthins.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PolicyRepository policyRepository;
    
    @Autowired
    private PolicyInfoRepository policyInfoRepository;
    
    @Autowired
    private ClaimRepository claimRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private InquiryRepository inquiryRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private CampaignRepository campaignRepository;
    
    @Autowired
    private PayrollRepository payrollRepository;
    
    @Autowired
    private PerformanceReviewRepository performanceReviewRepository;
    
    @Autowired
    private PaymentReminderRepository paymentReminderRepository;
    
    @Autowired
    private BankAccountRepository bankAccountRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Initializing Health Insurance Database...");
        
        // Check if data already exists
        if (userRepository.count() > 0) {
            System.out.println("✅ Database already initialized. Skipping data creation.");
            return;
        }

        // Create Users
        createUsers();
        
        // Create Policies
        createPolicies();
        
        // Create Claims
        createClaims();
        
        // Create Payments
        createPayments();
        
        // Create Employees
        createEmployees();
        
        // Create Inquiries
        createInquiries();
        
        // Create Audit Logs
        createAuditLogs();
        
        // Create Campaigns
        createCampaigns();
        
        // Create Payrolls
        createPayrolls();
        
        // Create Performance Reviews
        createPerformanceReviews();
        
        // Create Payment Reminders
        createPaymentReminders();
        
        System.out.println("✅ Database initialization completed successfully!");
        System.out.println("📊 Created tables: users, policies, claims, payments, employees, inquiries, audit_logs, campaigns, payrolls, performance_reviews, payment_reminders");
    }

    private void createUsers() {
        System.out.println("👥 Creating users...");
        
        // Create bank accounts for customers first
        BankAccount johnAccount = new BankAccount("Bank of Ceylon", "1001234567", "John Doe", "Colombo Main Branch");
        BankAccount janeAccount = new BankAccount("Commercial Bank", "2001234567", "Jane Smith", "Kandy Branch");
        
        bankAccountRepository.saveAll(Arrays.asList(johnAccount, janeAccount));
        System.out.println("✅ Created bank accounts for customers");
        
        // Create users
        User admin = new User("admin", passwordEncoder.encode("admin123"), "System Administrator", "admin@healthins.com", "admin@healthins.com", "+1234567890", true, Role.ADMIN);
        User johnDoe = new User("john_doe", passwordEncoder.encode("password123"), "John Doe", "john@email.com", "john@email.com", "+1234567891", true, Role.CUSTOMER);
        User janeSmith = new User("jane_smith", passwordEncoder.encode("password123"), "Jane Smith", "jane@email.com", "jane@email.com", "+1234567892", true, Role.POLICYHOLDER);
        User supportAgent = new User("support_agent", passwordEncoder.encode("support123"), "Support Agent", "support@healthins.com", "support@healthins.com", "+1234567893", true, Role.CUSTOMER_SERVICE);
        User claimsProcessor = new User("claims_processor", passwordEncoder.encode("claims123"), "Claims Processor", "claims@healthins.com", "claims@healthins.com", "+1234567894", true, Role.CLAIMS_PROCESSING);
        User marketingManager = new User("marketing_manager", passwordEncoder.encode("marketing123"), "Marketing Manager", "marketing@healthins.com", "marketing@healthins.com", "+1234567895", true, Role.MARKETING);
        User hrManager = new User("hr_manager", passwordEncoder.encode("hr123"), "HR Manager", "hr@healthins.com", "hr@healthins.com", "+1234567896", true, Role.HR);
        
        // Assign bank accounts to customers
        johnDoe.setBankAccount(johnAccount);
        janeSmith.setBankAccount(janeAccount);
        
        List<User> users = Arrays.asList(admin, johnDoe, janeSmith, supportAgent, claimsProcessor, marketingManager, hrManager);
        
        userRepository.saveAll(users);
        System.out.println("✅ Created " + users.size() + " users");
    }

    private void createPolicies() {
        System.out.println("📋 Creating policies...");
        
        User customer1 = userRepository.findByUsername("john_doe").orElse(null);
        User customer2 = userRepository.findByUsername("jane_smith").orElse(null);
        
        if (customer1 != null && customer2 != null) {
            List<Policy> policies = Arrays.asList(
                new Policy("POL-001", PolicyStatus.ACTIVE, new BigDecimal("15000.00"), LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(6), customer1, "Comprehensive Health Coverage"),
                new Policy("POL-002", PolicyStatus.ACTIVE, new BigDecimal("20000.00"), LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(9), customer2, "Premium Health Coverage"),
                new Policy("POL-003", PolicyStatus.PENDING, new BigDecimal("10000.00"), LocalDate.now().plusDays(30), LocalDate.now().plusMonths(12), customer1, "Basic Health Coverage")
            );
            
            policyRepository.saveAll(policies);
            System.out.println("✅ Created " + policies.size() + " policies");
        }
        
        // Create coverage types in policy_info table
        if (policyInfoRepository.count() == 0) {
            List<PolicyInfo> coverageTypes = Arrays.asList(
                new PolicyInfo(
                    "Basic Health Coverage",
                    "Essential health coverage for basic medical needs",
                    "• Outpatient consultations\n• Basic diagnostic tests\n• Generic medications\n• Emergency services\n• Preventive care",
                    "Up to LKR 5,000,000 per year"
                ),
                new PolicyInfo(
                    "Comprehensive Health Coverage",
                    "Complete health coverage for all medical needs",
                    "• All Basic coverage benefits\n• Specialist consultations\n• Advanced diagnostic procedures\n• Brand-name medications\n• Hospitalization coverage\n• Surgery coverage\n• Maternity care",
                    "Up to LKR 15,000,000 per year"
                ),
                new PolicyInfo(
                    "Premium Health Coverage",
                    "Premium coverage with extensive benefits and privileges",
                    "• All Comprehensive coverage benefits\n• VIP hospital rooms\n• Alternative treatments\n• International coverage\n• Dental and vision care\n• Mental health services\n• Wellness programs\n• 24/7 concierge medical service",
                    "Up to LKR 50,000,000 per year"
                ),
                new PolicyInfo(
                    "Family Health Coverage",
                    "Comprehensive family health insurance for up to 6 members",
                    "• Coverage for spouse and children\n• Pediatric care and vaccinations\n• Maternity and newborn care\n• Family wellness programs\n• Annual health check-ups for all members\n• Dental and vision care for children\n• Emergency ambulance services\n• Cashless hospitalization",
                    "Up to LKR 25,000,000 per year per family"
                ),
                new PolicyInfo(
                    "Senior Citizen Health Coverage",
                    "Specialized health coverage designed for individuals aged 60 and above",
                    "• Coverage for age-related conditions\n• Diabetes and hypertension management\n• Cardiac and orthopedic care\n• Home healthcare services\n• Pre-existing condition coverage\n• Domiciliary hospitalization\n• Health monitoring devices\n• No medical tests required",
                    "Up to LKR 10,000,000 per year"
                ),
                new PolicyInfo(
                    "Critical Illness Coverage",
                    "Protection against major critical illnesses with lump sum benefits",
                    "• Cancer treatment coverage\n• Heart attack and stroke coverage\n• Kidney failure treatment\n• Major organ transplant\n• Neurological disorders\n• Lump sum payout on diagnosis\n• Second medical opinion coverage\n• Rehabilitation support",
                    "Lump sum up to LKR 30,000,000"
                )
            );
            
            policyInfoRepository.saveAll(coverageTypes);
            System.out.println("✅ Created " + coverageTypes.size() + " coverage types in policy_info");
        }
    }

    private void createClaims() {
        System.out.println("🏥 Creating claims...");
        
        List<Policy> policies = policyRepository.findAll();
        if (!policies.isEmpty()) {
            List<Claim> claims = Arrays.asList(
                new Claim(),
                new Claim(),
                new Claim()
            );
            
            // Set claim properties
            claims.get(0).setClaimId("CLM-001");
            claims.get(0).setStatus(ClaimStatus.PENDING);
            claims.get(0).setAmount(50000.0);
            claims.get(0).setDocumentPath("/documents/claim1.pdf");
            claims.get(0).setClaimDate(LocalDate.now().minusDays(5));
            claims.get(0).setNotes("Medical treatment for flu");
            claims.get(0).setPolicy(policies.get(0));
            
            claims.get(1).setClaimId("CLM-002");
            claims.get(1).setStatus(ClaimStatus.UNDER_REVIEW);
            claims.get(1).setAmount(120000.0);
            claims.get(1).setDocumentPath("/documents/claim2.pdf");
            claims.get(1).setClaimDate(LocalDate.now().minusDays(10));
            claims.get(1).setNotes("Emergency surgery");
            claims.get(1).setPolicy(policies.get(1));
            
            claims.get(2).setClaimId("CLM-003");
            claims.get(2).setStatus(ClaimStatus.APPROVED);
            claims.get(2).setAmount(80000.0);
            claims.get(2).setDocumentPath("/documents/claim3.pdf");
            claims.get(2).setClaimDate(LocalDate.now().minusDays(15));
            claims.get(2).setNotes("Prescription medication");
            claims.get(2).setPolicy(policies.get(0));

            // Add a Premium Health claim
            Claim premiumClaim = new Claim();
            premiumClaim.setClaimId("CLM-004");
            premiumClaim.setStatus(ClaimStatus.APPROVED);
            premiumClaim.setAmount(250000.0);
            premiumClaim.setDocumentPath("/documents/claim4.pdf");
            premiumClaim.setClaimDate(LocalDate.now().minusDays(2));
            premiumClaim.setNotes("Premium Health: Hospital stay for surgery\nHospital: City General\nDoctor: Dr. Smith\nDiagnosis: Appendicitis\nTreatment: Laparoscopic Appendectomy");
            premiumClaim.setPolicy(policies.get(1));
            claims.add(premiumClaim);
            
            claimRepository.saveAll(claims);
            System.out.println("✅ Created " + claims.size() + " claims");
        }
    }

    private void createPayments() {
        System.out.println("💳 Creating payments...");
        
        List<Policy> policies = policyRepository.findAll();
        if (!policies.isEmpty()) {
            List<Payment> payments = Arrays.asList(
                new Payment(policies.get(0), new BigDecimal("15000.00"), LocalDate.now().minusDays(5), PaymentStatus.PAID, LocalDate.now().minusDays(5)),
                new Payment(policies.get(1), new BigDecimal("20000.00"), LocalDate.now().minusDays(3), PaymentStatus.PAID, LocalDate.now().minusDays(3)),
                new Payment(policies.get(0), new BigDecimal("15000.00"), LocalDate.now().plusDays(25), PaymentStatus.PENDING, null),
                new Payment(policies.get(1), new BigDecimal("20000.00"), LocalDate.now().plusDays(27), PaymentStatus.PENDING, null)
            );
            
            paymentRepository.saveAll(payments);
            System.out.println("✅ Created " + payments.size() + " payments");
        }
    }

    private void createEmployees() {
        System.out.println("👨‍💼 Creating employees...");
        
        User admin = userRepository.findByUsername("admin").orElse(null);
        User support = userRepository.findByUsername("support_agent").orElse(null);
        User claims = userRepository.findByUsername("claims_processor").orElse(null);
        User marketing = userRepository.findByUsername("marketing_manager").orElse(null);
        User hr = userRepository.findByUsername("hr_manager").orElse(null);
        
        if (admin != null && support != null && claims != null && marketing != null && hr != null) {
            List<Employee> employees = Arrays.asList(
                new Employee(admin, "IT", 7500000.0, LocalDate.now().minusYears(2), 5),
                new Employee(support, "Customer Service", 4500000.0, LocalDate.now().minusMonths(18), 4),
                new Employee(claims, "Claims Processing", 5000000.0, LocalDate.now().minusMonths(12), 4),
                new Employee(marketing, "Marketing", 6000000.0, LocalDate.now().minusMonths(8), 5),
                new Employee(hr, "Human Resources", 5500000.0, LocalDate.now().minusMonths(6), 4)
            );
            
            employeeRepository.saveAll(employees);
            System.out.println("✅ Created " + employees.size() + " employees");
        }
    }

    private void createInquiries() {
        System.out.println("❓ Creating inquiries...");
        
        User customer1 = userRepository.findByUsername("john_doe").orElse(null);
        User customer2 = userRepository.findByUsername("jane_smith").orElse(null);
        
        if (customer1 != null && customer2 != null) {
            List<Inquiry> inquiries = Arrays.asList(
                new Inquiry(customer1, "Billing", "Question about premium payment", "Premium Payment Issue", InquiryStatus.OPEN, null),
                new Inquiry(customer2, "Coverage", "What is covered under my policy?", "Coverage Question", InquiryStatus.RESOLVED, LocalDate.now().minusDays(2)),
                new Inquiry(customer1, "Claims", "Status of my claim submission", "Claim Status Inquiry", InquiryStatus.OPEN, null)
            );
            
            inquiryRepository.saveAll(inquiries);
            System.out.println("✅ Created " + inquiries.size() + " inquiries");
        }
    }

    private void createAuditLogs() {
        System.out.println("📝 Creating audit logs...");
        
        User admin = userRepository.findByUsername("admin").orElse(null);
        User support = userRepository.findByUsername("support_agent").orElse(null);
        
        if (admin != null && support != null) {
            List<AuditLog> auditLogs = Arrays.asList(
                new AuditLog(admin, "LOGIN", LocalDateTime.now().minusHours(2), "Admin login successful"),
                new AuditLog(support, "CLAIM_UPDATE", LocalDateTime.now().minusHours(1), "Updated claim status"),
                new AuditLog(admin, "USER_CREATED", LocalDateTime.now().minusMinutes(30), "Created new user account")
            );
            
            auditLogRepository.saveAll(auditLogs);
            System.out.println("✅ Created " + auditLogs.size() + " audit logs");
        }
    }

    private void createCampaigns() {
        System.out.println("📢 Creating campaigns...");
        
        List<Campaign> campaigns = Arrays.asList(
            new Campaign("Summer Health Check", CampaignType.EMAIL, LocalDate.now().minusDays(30), LocalDate.now().plusDays(30), CampaignStatus.ACTIVE, "Age 25-45, Active policies", "Promote summer health checkups with special offers"),
            new Campaign("New Year Premium Discount", CampaignType.SMS, LocalDate.now().minusDays(15), LocalDate.now().plusDays(15), CampaignStatus.ACTIVE, "All customers", "New Year premium discount campaign for all customers"),
            new Campaign("Wellness Program Launch", CampaignType.SOCIAL_MEDIA, LocalDate.now().plusDays(7), LocalDate.now().plusDays(37), CampaignStatus.DRAFT, "Health-conscious segment", "Launch of new wellness program targeting health-conscious customers")
        );
        
        campaignRepository.saveAll(campaigns);
        System.out.println("✅ Created " + campaigns.size() + " campaigns");
    }

    private void createPayrolls() {
        System.out.println("💰 Creating payrolls...");
        
        List<Employee> employees = employeeRepository.findAll();
        if (!employees.isEmpty()) {
            List<Payroll> payrolls = Arrays.asList(
                new Payroll(employees.get(0), new BigDecimal("625000.00"), LocalDate.now().minusDays(5), PayrollStatus.PAID),
                new Payroll(employees.get(1), new BigDecimal("375000.00"), LocalDate.now().minusDays(5), PayrollStatus.PAID),
                new Payroll(employees.get(2), new BigDecimal("416667.00"), LocalDate.now().minusDays(5), PayrollStatus.PAID),
                new Payroll(employees.get(3), new BigDecimal("500000.00"), LocalDate.now().minusDays(5), PayrollStatus.PAID),
                new Payroll(employees.get(4), new BigDecimal("458333.00"), LocalDate.now().minusDays(5), PayrollStatus.PAID)
            );
            
            payrollRepository.saveAll(payrolls);
            System.out.println("✅ Created " + payrolls.size() + " payrolls");
        }
    }

    private void createPerformanceReviews() {
        System.out.println("⭐ Creating performance reviews...");
        
        List<Employee> employees = employeeRepository.findAll();
        if (!employees.isEmpty()) {
            List<PerformanceReview> reviews = Arrays.asList(
                new PerformanceReview(LocalDate.now().minusDays(30), "Excellent performance in system administration", 5, employees.get(0)),
                new PerformanceReview(LocalDate.now().minusDays(25), "Good customer service skills", 4, employees.get(1)),
                new PerformanceReview(LocalDate.now().minusDays(20), "Efficient claims processing", 4, employees.get(2)),
                new PerformanceReview(LocalDate.now().minusDays(15), "Creative marketing campaigns", 5, employees.get(3)),
                new PerformanceReview(LocalDate.now().minusDays(10), "Effective HR management", 4, employees.get(4))
            );
            
            performanceReviewRepository.saveAll(reviews);
            System.out.println("✅ Created " + reviews.size() + " performance reviews");
        }
    }

    private void createPaymentReminders() {
        System.out.println("⏰ Creating payment reminders...");
        
        User customer1 = userRepository.findByUsername("john_doe").orElse(null);
        User customer2 = userRepository.findByUsername("jane_smith").orElse(null);
        
        if (customer1 != null && customer2 != null) {
            List<PaymentReminder> reminders = Arrays.asList(
                new PaymentReminder(customer1, LocalDate.now().plusDays(5), false),
                new PaymentReminder(customer2, LocalDate.now().plusDays(7), false),
                new PaymentReminder(customer1, LocalDate.now().plusDays(10), false)
            );
            
            paymentReminderRepository.saveAll(reminders);
            System.out.println("✅ Created " + reminders.size() + " payment reminders");
        }
    }
}
