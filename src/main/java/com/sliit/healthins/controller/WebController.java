package com.sliit.healthins.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@ControllerAdvice // Global exception handling for all controllers
public class WebController {

    // Landing page - redirect to login
    @GetMapping("/")
    public String home() {
        return "redirect:/login.html";
    }

    // Login page (public access)
    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }

    // Admin module
    @GetMapping("/admin-system-settings")
    public String adminSystemSettings() {
        return "redirect:/admin-system-settings.html";
    }

    // Claims Processing module
    @GetMapping("/claims-processing")
    public String claimsProcessing() {
        return "redirect:/claims-processing.html";
    }

    // Customer Support module
    @GetMapping("/customer-support")
    public String customerSupport() {
        return "redirect:/customer_support.html";
    }

    // Customer Portal module
    @GetMapping("/customer-portal")
    public String customerPortal() {
        return "redirect:/customer-portal.html";
    }

    // HR Management module
    @GetMapping("/hr-manager")
    public String hrManager() {
        return "redirect:/hr-manager.html";
    }

    // Marketing Manager module
    @GetMapping("/marketing-manager")
    public String marketingManager() {
        return "redirect:/marketing-manager.html";
    }

    // Dashboard is now handled by DashboardController

    // Logout redirect (handled by Spring Security)
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login.html";
    }

    // Global exception handler
    @SuppressWarnings("SpringMVCViewInspection")
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception ex, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error"); // Assumes an error.html in /static
        modelAndView.addObject("errorMessage", "An unexpected error occurred: " + ex.getMessage());
        modelAndView.addObject("url", request.getRequestURL());
        return modelAndView;
    }

    // Specific handler for 404 (Page Not Found)
    @SuppressWarnings("SpringMVCViewInspection")
    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ModelAndView handleNotFoundException(org.springframework.web.servlet.NoHandlerFoundException ex, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "Page not found: " + ex.getMessage());
        modelAndView.addObject("url", request.getRequestURL());
        modelAndView.addObject("status", 404);
        return modelAndView;
    }

    // Specific handler for 403 (Access Denied)
    @SuppressWarnings("SpringMVCViewInspection")
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ignoredEx, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "Access denied: You do not have permission to access this page.");
        modelAndView.addObject("url", request.getRequestURL());
        modelAndView.addObject("status", 403);
        return modelAndView;
    }
}