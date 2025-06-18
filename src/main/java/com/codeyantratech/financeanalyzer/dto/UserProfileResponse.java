package com.codeyantratech.financeanalyzer.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserProfileResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private BigDecimal monthlyBudgetLimit;
    private Boolean autoCategorizationEnabled;
    private String preferredCurrency;
    private Boolean notificationEmailEnabled;
    private Boolean notificationSmsEnabled;
} 