package com.sliit.healthins.pattern.command.impl;

import com.sliit.healthins.dto.CustomerDTO;
import com.sliit.healthins.model.User;
import com.sliit.healthins.pattern.command.CustomerSupportCommand;
import com.sliit.healthins.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Concrete Command: Update Customer Command
 * Implements the command pattern for customer update operations
 */
@Component
public class UpdateCustomerCommand implements CustomerSupportCommand {
    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    
    private Long customerId;
    private CustomerDTO customerDTO;
    private User previousState;
    
    @Autowired
    public UpdateCustomerCommand(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }
    
    public UpdateCustomerCommand setParameters(Long customerId, CustomerDTO customerDTO) {
        this.customerId = customerId;
        this.customerDTO = customerDTO;
        return this;
    }
    
    @Override
    public Object execute() {
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        
        // Store previous state for undo
        this.previousState = new User();
        modelMapper.map(user, this.previousState);
        
        // Update user
        modelMapper.map(customerDTO, user);
        user = userRepository.save(user);
        
        return modelMapper.map(user, CustomerDTO.class);
    }
    
    @Override
    public Object undo() {
        if (previousState == null) {
            throw new IllegalStateException("No previous state available for undo");
        }
        
        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
        
        modelMapper.map(previousState, user);
        user = userRepository.save(user);
        
        return modelMapper.map(user, CustomerDTO.class);
    }
    
    @Override
    public String getDescription() {
        return "Update customer information for ID: " + customerId;
    }
    
    @Override
    public boolean supportsUndo() {
        return true;
    }
}
