package com.codeyantratech.financeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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