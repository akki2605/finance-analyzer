package com.codeyantratech.financeanalyzer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserProfileRequest {
    @DecimalMin(value = "0.0", message = "Monthly budget limit must be positive")
    private BigDecimal monthlyBudgetLimit;

    private Boolean autoCategorizationEnabled;

    @Size(min = 3, max = 10, message = "Currency code must be between 3 and 10 characters")
    @Pattern(regexp = "^[A-Z]{3,10}$", message = "Currency code must be uppercase letters")
    private String preferredCurrency;

    private Boolean notificationEmailEnabled;
    
    private Boolean notificationSmsEnabled;

    // Optional: First name and last name update
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
} 