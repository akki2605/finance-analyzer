package com.codeyantratech.financeanalyzer.service;

import com.codeyantratech.financeanalyzer.model.Category;
import com.codeyantratech.financeanalyzer.model.User;
import com.codeyantratech.financeanalyzer.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Service class responsible for managing transaction categories.
 * Handles both initial default categories and user-created categories.
 * All categories are user-specific and can be modified or deleted.
 */
@Slf4j
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserService userService;

    // Default categories template with their colors
    private static final List<DefaultCategory> DEFAULT_CATEGORIES = Arrays.asList(
        new DefaultCategory("Food & Groceries", "Essential food and grocery expenses", "#4CAF50"),
        new DefaultCategory("Rent & Bills", "Housing rent and utility bills", "#2196F3"),
        new DefaultCategory("Transport", "Transportation and travel expenses", "#FF9800"),
        new DefaultCategory("Shopping", "General shopping and purchases", "#E91E63"),
        new DefaultCategory("Health", "Healthcare and medical expenses", "#F44336"),
        new DefaultCategory("Entertainment", "Entertainment and leisure activities", "#9C27B0"),
        new DefaultCategory("Education", "Educational expenses and materials", "#3F51B5"),
        new DefaultCategory("Others", "Miscellaneous expenses", "#607D8B")
    );

    /**
     * Creates default categories for a new user.
     * Should be called when a new user is registered.
     *
     * @param user The newly registered user
     */
    @Transactional
    public void createDefaultCategoriesForUser(User user) {
        for (DefaultCategory defaultCat : DEFAULT_CATEGORIES) {
            Category category = new Category();
            category.setName(defaultCat.name);
            category.setDescription(defaultCat.description);
            category.setColor(defaultCat.color);
            category.setUser(user);
            categoryRepository.save(category);
            log.info("Created default category '{}' for user: {}", defaultCat.name, user.getUsername());
        }
    }

    /**
     * Creates a new category for a user.
     */
    @Transactional
    public Category createCategory(String name, String description, String color, String username) {
        User user = userService.getCurrentUser(username);
        
        // Check if user already has a category with this name
        if (categoryRepository.existsByNameAndUser(name, user)) {
            throw new RuntimeException("You already have a category with this name");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setColor(color);
        category.setUser(user);

        return categoryRepository.save(category);
    }

    /**
     * Updates an existing category.
     * Users can only update their own categories.
     */
    @Transactional
    public Category updateCategory(Long categoryId, String name, String description, String color, String username) {
        Category category = getCategoryById(categoryId, username);
        User user = userService.getCurrentUser(username);

        // Check if new name conflicts with existing category (excluding current category)
        if (!category.getName().equals(name) && categoryRepository.existsByNameAndUser(name, user)) {
            throw new RuntimeException("You already have a category with this name");
        }

        category.setName(name);
        category.setDescription(description);
        category.setColor(color);

        return categoryRepository.save(category);
    }

    /**
     * Deletes a category.
     * Users can only delete their own categories.
     */
    @Transactional
    public void deleteCategory(Long categoryId, String username) {
        Category category = getCategoryById(categoryId, username);
        categoryRepository.delete(category);
    }

    /**
     * Gets all categories for a user.
     */
    @Transactional(readOnly = true)
    public List<Category> getUserCategories(String username) {
        User user = userService.getCurrentUser(username);
        return categoryRepository.findByUserOrderByNameAsc(user);
    }

    /**
     * Gets a specific category by ID.
     * Users can only access their own categories.
     */
    @Transactional(readOnly = true)
    public Category getCategoryById(Long categoryId, String username) {
        User user = userService.getCurrentUser(username);
        return categoryRepository.findByIdAndUser(categoryId, user)
            .orElseThrow(() -> new RuntimeException("Category not found or access denied"));
    }

    // Helper class for defining default categories template
        private record DefaultCategory(String name, String description, String color) {
    }
} 