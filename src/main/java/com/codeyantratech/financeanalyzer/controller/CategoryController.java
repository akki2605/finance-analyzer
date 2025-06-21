package com.codeyantratech.financeanalyzer.controller;

import com.codeyantratech.financeanalyzer.dto.ApiResponse;
import com.codeyantratech.financeanalyzer.dto.CategoryRequest;
import com.codeyantratech.financeanalyzer.dto.CategoryResponse;
import com.codeyantratech.financeanalyzer.model.Category;
import com.codeyantratech.financeanalyzer.security.UserPrincipal;
import com.codeyantratech.financeanalyzer.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing transaction categories.
 * Provides endpoints for viewing and managing user-specific categories.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Lists all categories for the current user.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Category> categories = categoryService.getUserCategories(userPrincipal.getUsername());
        List<CategoryResponse> response = categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new category for the user.
     */
    @PostMapping("/category/custom")
    public ResponseEntity<ApiResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Category category = categoryService.createCategory(
                request.getName(),
                request.getDescription(),
                request.getColor(),
                userPrincipal.getUsername()
        );
        return ResponseEntity.ok(ApiResponse.success("Category created", toResponse(category)));
    }

    /**
     * Updates an existing category.
     * Users can only update their own categories.
     */
    @PutMapping("/category/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Category category = categoryService.updateCategory(
                id,
                request.getName(),
                request.getDescription(),
                request.getColor(),
                userPrincipal.getUsername()
        );
        return ResponseEntity.ok(ApiResponse.success("Category updated", toResponse(category)));
    }

    /**
     * Deletes a category.
     * Users can only delete their own categories.
     */
    @DeleteMapping("/category/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        categoryService.deleteCategory(id, userPrincipal.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Category deleted"));
    }

    /**
     * Gets a specific category by ID.
     * Users can only view their own categories.
     */
    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryResponse> getCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Category category = categoryService.getCategoryById(id, userPrincipal.getUsername());
        return ResponseEntity.ok(toResponse(category));
    }

    // Helper method to convert Category to CategoryResponse
    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .color(category.getColor())
                .userId(category.getUser() != null ? category.getUser().getId() : null)
                .build();
    }
} 