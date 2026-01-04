# Lanka Health Insurance - Entity Relationship Diagram

## Database Schema Overview

```mermaid
erDiagram
    USERS {
        BIGINT id PK
        VARCHAR username UK
        VARCHAR password
        VARCHAR name
        VARCHAR contact
        VARCHAR email UK
        VARCHAR phone
        BOOLEAN is_active
        ENUM role
        BIGINT bank_account_id FK
        DATETIME created_at
        DATETIME updated_at
    }

    BANK_ACCOUNTS {
        BIGINT id PK
        VARCHAR bank_name
        VARCHAR account_number UK
        VARCHAR account_holder_name
        VARCHAR branch
        DATETIME created_at
        DATETIME updated_at
    }

    POLICIES {
        BIGINT id PK
        VARCHAR policy_number UK
        ENUM status
        DECIMAL premium_amount
        DATE start_date
        DATE end_date
        BIGINT user_id FK
        TEXT coverage
        DATETIME created_at
        DATETIME updated_at
    }

    CLAIMS {
        BIGINT id PK
        VARCHAR claim_id UK
        ENUM status
        DOUBLE amount
        VARCHAR document_path
        DATE claim_date
        TEXT notes
        BIGINT policy_id FK
        DATETIME created_at
        DATETIME updated_at
    }

    PAYMENTS {
        BIGINT id PK
        BIGINT policy_id FK
        DECIMAL amount
        DATE due_date
        ENUM status
        DATE payment_date
        DATETIME created_at
        DATETIME updated_at
    }

    INQUIRIES {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR type
        TEXT description
        VARCHAR title
        ENUM status
        DATE resolution_date
        DATETIME created_at
        DATETIME updated_at
    }

    EMPLOYEES {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR department
        DOUBLE salary
        DATE hire_date
        INTEGER performance_rating
        DATETIME created_at
        DATETIME updated_at
    }

    CAMPAIGNS {
        BIGINT id PK
        VARCHAR name
        ENUM type
        DATE start_date
        DATE end_date
        ENUM status
        TEXT target_segment
        TEXT description
        DATETIME created_at
        DATETIME updated_at
    }

    AUDIT_LOGS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR action
        DATETIME timestamp
        TEXT details
    }

    CUSTOMER_SEGMENTS {
        BIGINT id PK
        VARCHAR name
        TEXT criteria
        INTEGER customer_count
        DATETIME created_at
        DATETIME updated_at
    }

    PAYMENT_REMINDERS {
        BIGINT id PK
        BIGINT user_id FK
        VARCHAR message
        DATE reminder_date
        BOOLEAN sent
        DATETIME created_at
        DATETIME updated_at
    }

    PAYROLLS {
        BIGINT id PK
        BIGINT employee_id FK
        DOUBLE basic_salary
        DOUBLE allowances
        DOUBLE deductions
        DOUBLE net_salary
        DATE pay_period_start
        DATE pay_period_end
        ENUM status
        DATETIME created_at
        DATETIME updated_at
    }

    PERFORMANCE_REVIEWS {
        BIGINT id PK
        BIGINT employee_id FK
        INTEGER rating
        TEXT feedback
        DATE review_date
        DATETIME created_at
        DATETIME updated_at
    }

    %% Relationships
    USERS ||--o{ POLICIES : "has"
    USERS ||--o{ INQUIRIES : "submits"
    USERS ||--o| BANK_ACCOUNTS : "owns"
    USERS ||--o| EMPLOYEES : "is"
    USERS ||--o{ AUDIT_LOGS : "performs"
    USERS ||--o{ PAYMENT_REMINDERS : "receives"

    POLICIES ||--o{ CLAIMS : "generates"
    POLICIES ||--o{ PAYMENTS : "requires"

    EMPLOYEES ||--o{ PAYROLLS : "receives"
    EMPLOYEES ||--o{ PERFORMANCE_REVIEWS : "undergoes"
```

## Entity Descriptions

### Core Entities

**USERS**
- Primary entity representing all system users (customers, employees, admins)
- Contains authentication and contact information
- Links to bank accounts for payment processing

**POLICIES** 
- Insurance policies purchased by customers
- Contains coverage details, premium amounts, and validity periods
- Central entity linking customers to claims and payments

**CLAIMS**
- Insurance claims submitted by policyholders
- Tracks claim processing workflow with status updates
- Links to policies and contains supporting documentation

**PAYMENTS**
- Premium payments and claim disbursements
- Tracks payment status and due dates
- Links to policies for premium tracking

### Supporting Entities

**BANK_ACCOUNTS**
- Customer banking information for payments
- One-to-one relationship with users
- Used for claim disbursements and premium collections

**INQUIRIES**
- Customer support tickets and inquiries
- Tracks resolution status and customer communications
- Links to users for customer service management

**EMPLOYEES**
- Staff information extending user entity
- Contains HR-specific data like salary and department
- Links to payroll and performance management

### Administrative Entities

**CAMPAIGNS**
- Marketing campaigns and promotions
- Tracks campaign lifecycle and target segments
- Used for customer acquisition and retention

**AUDIT_LOGS**
- System activity tracking for compliance
- Records user actions and system events
- Essential for security and regulatory requirements

**CUSTOMER_SEGMENTS**
- Marketing segmentation for targeted campaigns
- Groups customers based on criteria
- Supports personalized marketing efforts

## Key Relationships

1. **User-Policy (1:N)**: Users can have multiple insurance policies
2. **Policy-Claim (1:N)**: Each policy can generate multiple claims
3. **Policy-Payment (1:N)**: Policies require multiple premium payments
4. **User-BankAccount (1:1)**: Each user has one bank account for transactions
5. **User-Employee (1:1)**: Staff users have extended employee information
6. **Employee-Payroll (1:N)**: Employees receive multiple payroll records
7. **User-Inquiry (1:N)**: Users can submit multiple support inquiries

## Enums and Status Values

**Role**: ADMIN, HR_MANAGER, MARKETING_MANAGER, CUSTOMER_SUPPORT, POLICYHOLDER, CLAIMS_EXECUTIVE
**PolicyStatus**: ACTIVE, EXPIRED, CANCELLED, SUSPENDED
**ClaimStatus**: PENDING, UNDER_REVIEW, APPROVED, PAID, REJECTED
**PaymentStatus**: PENDING, COMPLETED, FAILED, OVERDUE
**InquiryStatus**: OPEN, IN_PROGRESS, RESOLVED, CLOSED
**CampaignStatus**: DRAFT, ACTIVE, PAUSED, COMPLETED, CANCELLED
**CampaignType**: EMAIL, SOCIAL_MEDIA, SMS, DIRECT_MAIL

## Database Constraints

- All primary keys are auto-generated BIGINT
- Unique constraints on username, email, policy_number, claim_id, account_number
- Foreign key constraints maintain referential integrity
- Audit fields (created_at, updated_at) track record lifecycle
- Enum constraints ensure data consistency for status fields