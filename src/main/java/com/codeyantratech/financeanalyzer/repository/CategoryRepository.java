package com.codeyantratech.financeanalyzer.repository;

import com.codeyantratech.financeanalyzer.model.Category;
import com.codeyantratech.financeanalyzer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Category entities.
 * All categories are user-specific.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * Finds all categories for a specific user, ordered by name.
     */
    List<Category> findByUserOrderByNameAsc(User user);

    /**
     * Finds a specific category by ID and user.
     */
    Optional<Category> findByIdAndUser(Long id, User user);

    /**
     * Checks if a user already has a category with the given name.
     */
    boolean existsByNameAndUser(String name, User user);
}