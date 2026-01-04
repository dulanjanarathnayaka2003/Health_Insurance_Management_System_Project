package com.sliit.healthins.pattern.config;

import com.sliit.healthins.pattern.decorator.impl.LoggingDTOFactoryDecorator;
import com.sliit.healthins.pattern.factory.DTOFactory;
import com.sliit.healthins.pattern.factory.impl.StandardDTOFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for design patterns
 * This class wires up all the design pattern components
 */
@Configuration
public class PatternConfiguration {
    
    /**
     * Creates the decorated DTO factory with logging capabilities
     * @param modelMapper ModelMapper instance
     * @return Decorated DTO factory
     */
    @Bean
    @Primary
    public DTOFactory dtoFactory(org.modelmapper.ModelMapper modelMapper) {
        DTOFactory standardFactory = new StandardDTOFactory(modelMapper);
        return new LoggingDTOFactoryDecorator(standardFactory);
    }
}
