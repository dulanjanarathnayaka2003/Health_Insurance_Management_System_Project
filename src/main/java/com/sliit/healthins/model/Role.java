package com.sliit.healthins.model;

import jakarta.annotation.Resource;

public enum Role {
    CUSTOMER, POLICYHOLDER, CUSTOMER_SERVICE_OFFICER, ADMIN, SUPPORT,IT_MANAGER, CUSTOMER_SERVICE, CLAIMS_PROCESSING, MARKETING, HR, USER;

    public static Resource ACTIVE;

    public String toUpperCase() {
        return this.name().toUpperCase();
    }
}
