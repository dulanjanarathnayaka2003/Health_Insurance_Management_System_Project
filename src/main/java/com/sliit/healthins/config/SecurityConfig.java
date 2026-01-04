package com.sliit.healthins.config;

import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity() // Enables @PreAuthorize and @Secured
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless APIs; enable with token in production
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll() // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll() // Authentication endpoints
                        .requestMatchers("/api/customer_support/**").hasRole("CUSTOMER_SERVICE") // Customer service API endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin-only API endpoints
                        .requestMatchers("/api/claims/**").hasRole("CLAIMS_PROCESSING") // Claims processing API endpoints
                        .requestMatchers("/admin-system-settings.html").hasRole("ADMIN") // Static HTML pages
                        .requestMatchers("/claims-processing.html").hasRole("CLAIMS_PROCESSING")
                        .requestMatchers("/customer-support.html").hasRole("CUSTOMER_SERVICE")
                        .requestMatchers("/customer_support.html").hasRole("CUSTOMER_SERVICE")
                        .requestMatchers("/hr-manager.html").hasRole("HR")
                        .requestMatchers("/marketing-manager.html").hasRole("MARKETING")
                        .requestMatchers("/customer-portal.html").hasAnyRole("CUSTOMER", "POLICYHOLDER")
                        .requestMatchers("/login.html").permitAll() // Public login page
                        .requestMatchers("/dashboard").authenticated() // Role-based dashboard
                        .requestMatchers("/dashboard.html").authenticated() // Requires any authenticated user
                        .requestMatchers("/*.css", "/*.js", "/*.jpg", "/*.jpeg", "/*.png", "/*.gif", "/*.svg", "/*.ico").permitAll() // Allow static resources
                        .requestMatchers("/images/**", "/css/**", "/js/**").permitAll() // Allow static resource folders
                        .anyRequest().authenticated() // All other requests need authentication
                )
                  // Enable basic authentication
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // Allow sessions for form login
                .formLogin(form -> form
                        .loginPage("/login.html") // Custom login page
                        .loginProcessingUrl("/login") // Form submission URL
                        .successHandler(authenticationSuccessHandler()) // Custom success handler
                        .failureUrl("/login.html?error=true") // Redirect back to login on failure
                        .permitAll() // Allow access to the login page
                )
                .logout(logout -> logout
                        .permitAll() // Allow logout for all
                        .logoutUrl("/logout") // Default logout URL
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, e) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(403);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\": \"Access Denied\"}");
                            } else {
                                response.sendError(403, "Access Denied");
                            }
                        })
                        .authenticationEntryPoint((request, response, e) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(401);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\": \"Unauthorized - Please login\"}");
                            } else {
                                response.sendRedirect("/login.html");
                            }
                        })
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            
            // Auto-activate customers and policyholders if they exist in database
            // This ensures existing customers can log in
            if ((user.getRole() == com.sliit.healthins.model.Role.CUSTOMER || 
                 user.getRole() == com.sliit.healthins.model.Role.POLICYHOLDER) && 
                !user.isActive()) {
                user.setActive(true);
                userRepository.save(user);
            }
            
            return new CustomUserDetails(user);
        };
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                String username = authentication.getName();
                User user = userRepository.findByUsername(username).orElse(null);
                
                if (user != null) {
                    // Ensure customer/policyholder is activated after successful login
                    if ((user.getRole() == com.sliit.healthins.model.Role.CUSTOMER || 
                         user.getRole() == com.sliit.healthins.model.Role.POLICYHOLDER) && 
                        !user.isActive()) {
                        user.setActive(true);
                        userRepository.save(user);
                    }
                    
                    switch (user.getRole()) {
                        case ADMIN:
                            response.sendRedirect("/admin-system-settings.html");
                            break;
                        case MARKETING:
                            response.sendRedirect("/marketing-manager.html");
                            break;
                        case HR:
                            response.sendRedirect("/hr-manager.html");
                            break;
                        case CUSTOMER_SERVICE:
                            response.sendRedirect("/customer_support.html");
                            break;
                        case CLAIMS_PROCESSING:
                            response.sendRedirect("/claims-processing.html");
                            break;
                        case CUSTOMER:
                        case POLICYHOLDER:
                            response.sendRedirect("/customer-portal.html");
                            break;
                        default:
                            response.sendRedirect("/dashboard");
                            break;
                    }
                } else {
                    response.sendRedirect("/dashboard");
                }
            }
        };
    }
}
    
