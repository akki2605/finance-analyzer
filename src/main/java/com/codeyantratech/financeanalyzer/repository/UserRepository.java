package com.codeyantratech.financeanalyzer.repository;

import com.codeyantratech.financeanalyzer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Provides methods for accessing and manipulating user records in the database.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * Used primarily for authentication and user lookup.
     *
     * @param username The username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     * Used for password reset and email verification.
     *
     * @param email The email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a username already exists.
     * Used during user registration to prevent duplicate usernames.
     *
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    Boolean existsByUsername(String username);

    /**
     * Checks if an email address is already registered.
     * Used during user registration to prevent duplicate email addresses.
     *
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    Boolean existsByEmail(String email);
}