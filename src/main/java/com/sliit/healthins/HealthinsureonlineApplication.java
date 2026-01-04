package com.sliit.healthins;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@ServletComponentScan
@EnableScheduling
public class HealthinsureonlineApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HealthinsureonlineApplication.class);
		app.addListeners((ApplicationListener<ApplicationReadyEvent>) event -> {
            LocalDateTime now = LocalDateTime.now();
            String startupTime = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            System.out.println("Health Insurance Online Application Started at: " + startupTime);
            System.out.println("Application Status: RUNNING");
            Runtime runtime = Runtime.getRuntime();
            System.out.println("Available processors (cores): " + runtime.availableProcessors());
            System.out.println("Free memory (bytes): " + runtime.freeMemory());
            System.out.println("Maximum memory (bytes): " + runtime.maxMemory());
        });

		ConfigurableApplicationContext context = app.run(args);

		context.addApplicationListener((ApplicationListener<ContextRefreshedEvent>) event -> System.out.println("Application context initialized and ready!"));
	}

	@Bean
	public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
		return factory -> {
			factory.addConnectorCustomizers(connector -> {
				connector.setProperty("relaxedPathChars", "[]|");
				connector.setProperty("relaxedQueryChars", "[]|{}^\\`\"<>");
			});
			factory.addContextCustomizers(context -> context.setPath(""));
		};
	}
}
