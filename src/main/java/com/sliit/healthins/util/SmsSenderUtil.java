package com.sliit.healthins.util;

import org.springframework.stereotype.Component;

@Component
public class SmsSenderUtil {
    // In a real system, use a third-party SMS API integration here
    public boolean sendSms(String to, String message) {
        System.out.println("Sending SMS to " + to + ": " + message);
        // simulate sending for now
        return true;
    }
}
