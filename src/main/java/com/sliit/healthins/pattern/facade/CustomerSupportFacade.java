package com.sliit.healthins.pattern.facade;

import com.sliit.healthins.dto.*;
import com.sliit.healthins.pattern.command.CustomerSupportCommand;
import com.sliit.healthins.pattern.command.impl.SendPaymentReminderCommand;
import com.sliit.healthins.pattern.command.impl.UpdateCustomerCommand;
import com.sliit.healthins.pattern.observer.CustomerSupportEvent;
import com.sliit.healthins.pattern.observer.CustomerSupportObserver;
import com.sliit.healthins.pattern.strategy.ReportGenerationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade Pattern: Customer Support Facade
 * Provides a simplified interface to complex customer support operations
 * Hides the complexity of multiple subsystems behind a single interface
 */
@Service
public class CustomerSupportFacade {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerSupportFacade.class);
    
    private final UpdateCustomerCommand updateCustomerCommand;
    private final SendPaymentReminderCommand sendPaymentReminderCommand;
    private final List<CustomerSupportObserver> observers;
    private final Map<String, ReportGenerationStrategy> reportStrategies;
    
    @Autowired
    public CustomerSupportFacade(UpdateCustomerCommand updateCustomerCommand,
                                SendPaymentReminderCommand sendPaymentReminderCommand,
                                List<CustomerSupportObserver> observers,
                                List<ReportGenerationStrategy> strategies) {
        this.updateCustomerCommand = updateCustomerCommand;
        this.sendPaymentReminderCommand = sendPaymentReminderCommand;
        this.observers = observers;
        
        // Initialize strategy map
        this.reportStrategies = new HashMap<>();
        for (ReportGenerationStrategy strategy : strategies) {
            reportStrategies.put(strategy.getReportType(), strategy);
        }
    }
    
    /**
     * Updates customer information using command pattern
     * @param customerId Customer ID
     * @param customerDTO Customer data
     * @param userId User performing the operation
     * @return Updated customer DTO
     */
    public CustomerDTO updateCustomer(Long customerId, CustomerDTO customerDTO, String userId) {
        logger.info("Facade: Updating customer ID: {} by user: {}", customerId, userId);
        
        CustomerSupportCommand command = updateCustomerCommand.setParameters(customerId, customerDTO);
        CustomerDTO result = (CustomerDTO) command.execute();
        
        // Notify observers
        notifyObservers(new CustomerSupportEvent(
            "CUSTOMER_UPDATE", 
            command.getDescription(), 
            userId,
            Map.of("customerId", customerId, "result", result)
        ));
        
        return result;
    }
    
    /**
     * Sends payment reminder using command pattern
     * @param reminderDTO Reminder data
     * @param userId User performing the operation
     * @return Result message
     */
    public String sendPaymentReminder(PaymentReminderDTO reminderDTO, String userId) {
        logger.info("Facade: Sending payment reminder for payment ID: {} by user: {}", 
                   reminderDTO.getPaymentId(), userId);
        
        CustomerSupportCommand command = sendPaymentReminderCommand.setParameters(reminderDTO);
        String result = (String) command.execute();
        
        // Notify observers
        notifyObservers(new CustomerSupportEvent(
            "PAYMENT_REMINDER", 
            command.getDescription(), 
            userId,
            Map.of("paymentId", reminderDTO.getPaymentId(), "reminderType", reminderDTO.getReminderType())
        ));
        
        return result;
    }
    
    /**
     * Generates report using strategy pattern
     * @param reportType Type of report
     * @param fromDate Start date
     * @param toDate End date
     * @param userId User requesting the report
     * @return Report data
     */
    public List<?> generateReport(String reportType, LocalDate fromDate, LocalDate toDate, String userId) {
        logger.info("Facade: Generating {} report from {} to {} by user: {}", 
                   reportType, fromDate, toDate, userId);
        
        ReportGenerationStrategy strategy = reportStrategies.get(reportType.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported report type: " + reportType);
        }
        
        List<?> result = strategy.generateReportData(fromDate, toDate);
        
        // Notify observers
        notifyObservers(new CustomerSupportEvent(
            "REPORT_GENERATION", 
            "Generated " + reportType + " report", 
            userId,
            Map.of("reportType", reportType, "recordCount", result.size())
        ));
        
        return result;
    }
    
    /**
     * Notifies all observers about an event
     * @param event The event to notify about
     */
    private void notifyObservers(CustomerSupportEvent event) {
        for (CustomerSupportObserver observer : observers) {
            try {
                observer.onCustomerSupportEvent(event);
            } catch (Exception e) {
                logger.error("Observer {} failed to handle event: {}", observer.getObserverName(), event, e);
            }
        }
    }
    
    /**
     * Gets available report types
     * @return List of available report types
     */
    public List<String> getAvailableReportTypes() {
        return List.copyOf(reportStrategies.keySet());
    }
}


