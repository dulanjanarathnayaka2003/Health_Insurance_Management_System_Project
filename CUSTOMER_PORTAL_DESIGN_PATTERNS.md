# Customer Portal Design Patterns

This document describes two new design patterns implemented for the Customer Portal module without modifying existing Java classes.

## 1. Adapter Pattern - Payment Gateway Integration

### Purpose
The Adapter pattern allows the customer portal to work with different payment gateways through a unified interface, making it easy to add new payment methods without changing existing code.

### Structure
```
PaymentGateway (Interface)
├── CreditCardGatewayAdapter
├── BankTransferGatewayAdapter
└── PaymentGatewayFactory
```

### Components

#### PaymentGateway Interface
- Defines standard payment operations: `processPayment()`, `verifyPayment()`, `getGatewayName()`
- Acts as the target interface that the customer portal expects

#### Concrete Adapters
- **CreditCardGatewayAdapter**: Handles credit card payments with transaction IDs starting with "CC-"
- **BankTransferGatewayAdapter**: Handles bank transfers with transaction IDs starting with "BT-"

#### PaymentGatewayFactory
- Creates appropriate gateway adapters based on payment method
- Supports "CREDIT_CARD", "CARD", "BANK_TRANSFER", "BANK" payment types

#### CustomerPortalPaymentService
- Integrates gateway adapters with existing customer portal functionality
- Provides `processPaymentWithGateway()` and `verifyPaymentStatus()` methods

### Benefits
- **Flexibility**: Easy to add new payment gateways without modifying existing code
- **Consistency**: All payment methods follow the same interface
- **Maintainability**: Each payment gateway is isolated in its own adapter
- **Integration**: Works seamlessly with existing CustomerPortalService

### Usage Example
```java
// In a controller or service
PaymentDTO payment = new PaymentDTO();
payment.setAmount(100.0);

// Process using credit card
PaymentDTO result = customerPortalPaymentService.processPaymentWithGateway(payment, "CREDIT_CARD");

// Verify payment
boolean isValid = customerPortalPaymentService.verifyPaymentStatus(result.getTransactionId(), "CREDIT_CARD");
```

## 2. Template Method Pattern - Customer Portal Operations

### Purpose
The Template Method pattern standardizes the workflow for all customer portal operations, ensuring consistent validation, permission checking, logging, and error handling across different operations.

### Structure
```
CustomerPortalOperation<T, R> (Abstract Class)
├── ClaimSubmissionOperation
├── PolicyPurchaseOperation
└── InquirySubmissionOperation
```

### Workflow Steps
1. **User Validation**: Verify user exists and is active
2. **Request Validation**: Validate input data
3. **Permission Check**: Verify user has permission for the operation
4. **Business Logic Execution**: Execute the main operation
5. **Operation Logging**: Log the operation for audit purposes
6. **Notification**: Send notifications if required
7. **Error Handling**: Handle any errors that occur

### Components

#### CustomerPortalOperation (Abstract Class)
- Defines the template method `execute()` with the standard workflow
- Provides hooks for subclasses to implement specific logic
- Handles common concerns like logging and error handling

#### Concrete Implementations
- **ClaimSubmissionOperation**: Handles claim submission with policy validation
- **PolicyPurchaseOperation**: Handles policy purchases with payment validation
- **InquirySubmissionOperation**: Handles customer inquiries with content validation

#### CustomerPortalOperationManager
- Coordinates different template method operations
- Provides a unified interface for executing operations
- Integrates with existing services

### Benefits
- **Consistency**: All operations follow the same workflow
- **Maintainability**: Common logic is centralized in the template
- **Extensibility**: Easy to add new operations by extending the template
- **Reliability**: Standardized error handling and validation
- **Auditability**: Consistent logging across all operations

### Usage Example
```java
// In a controller
@PostMapping("/claims-template")
public ResponseEntity<Claim> submitClaimWithTemplate(@RequestBody ClaimSubmissionDTO dto,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
    User user = userDetails.getUser();
    Claim claim = customerPortalOperationManager.executeClaimSubmission(dto, user.getId());
    return ResponseEntity.ok(claim);
}
```

## Integration with Existing Code

Both patterns are designed to work alongside existing functionality:

### Adapter Pattern Integration
- Can be used in CustomerPortalController for enhanced payment processing
- Complements existing PaymentDTO and payment-related methods
- Does not modify existing CustomerPortalService methods

### Template Method Integration
- Can be used as an alternative to direct service calls
- Provides additional validation and standardization
- Maintains compatibility with existing DTOs and models

## File Structure
```
src/main/java/com/sliit/healthins/pattern/
├── adapter/
│   ├── PaymentGateway.java
│   ├── PaymentGatewayFactory.java
│   ├── CustomerPortalPaymentService.java
│   └── impl/
│       ├── CreditCardGatewayAdapter.java
│       └── BankTransferGatewayAdapter.java
└── template/
    ├── CustomerPortalOperation.java
    ├── CustomerPortalOperationManager.java
    └── impl/
        ├── ClaimSubmissionOperation.java
        ├── PolicyPurchaseOperation.java
        └── InquirySubmissionOperation.java
```

## Configuration
Both patterns use Spring's dependency injection and are automatically configured through component scanning. No additional configuration is required.

## Testing
The patterns can be tested independently:
- Adapter pattern: Test different payment gateways and factory selection
- Template method pattern: Test operation workflow and validation steps

## Future Enhancements
- **Adapter Pattern**: Add more payment gateways (PayPal, Stripe, etc.)
- **Template Method Pattern**: Add more customer portal operations (profile updates, document uploads, etc.)