package com.codeyantratech.financeanalyzer.controller;

import com.codeyantratech.financeanalyzer.dto.ApiResponse;
import com.codeyantratech.financeanalyzer.dto.UserProfileRequest;
import com.codeyantratech.financeanalyzer.dto.UserProfileResponse;
import com.codeyantratech.financeanalyzer.model.User;
import com.codeyantratech.financeanalyzer.security.UserPrincipal;
import com.codeyantratech.financeanalyzer.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing user profile settings.
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    @Autowired
    private UserService userService;

    /**
     * Get the current user's profile settings
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.getCurrentUser(userPrincipal.getUsername());
        return ResponseEntity.ok(toProfileResponse(user));
    }

    /**
     * Update the current user's profile settings
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateUserProfile(
            @Valid @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User updatedUser = userService.updateUserProfile(userPrincipal.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", toProfileResponse(updatedUser)));
    }

    private UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .monthlyBudgetLimit(user.getMonthlyBudgetLimit())
                .autoCategorizationEnabled(user.getAutoCategorizationEnabled())
                .preferredCurrency(user.getPreferredCurrency())
                .notificationEmailEnabled(user.getNotificationEmailEnabled())
                .notificationSmsEnabled(user.getNotificationSmsEnabled())
                .build();
    }
} 