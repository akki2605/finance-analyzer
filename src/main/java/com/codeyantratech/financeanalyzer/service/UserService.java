package com.codeyantratech.financeanalyzer.service;

import com.codeyantratech.financeanalyzer.model.User;
import com.codeyantratech.financeanalyzer.repository.UserRepository;
import com.codeyantratech.financeanalyzer.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class responsible for managing user operations and authentication.
 * Implements UserDetailsService for Spring Security integration.
 * Handles user CRUD operations, authentication, and profile management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Loads a user by username for Spring Security authentication.
     * Required by UserDetailsService interface.
     *
     * @param username The username to look up
     * @return UserDetails object for Spring Security
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return UserPrincipal.create(user);
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email The email to search for
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds a user by their ID.
     *
     * @param id The user ID to search for
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Checks if a username is already taken.
     *
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Checks if an email is already registered.
     *
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Creates a new user account.
     * Validates that username and email are not already taken.
     *
     * @param username  The desired username
     * @param email     The user's email address
     * @param password  The user's password (will be encoded)
     * @param firstName The user's first name
     * @param lastName  The user's last name
     * @return The created user entity
     * @throws RuntimeException if username or email is already taken
     */
    @Transactional
    public User createUser(String username, String email, String password, String firstName, String lastName) {
        // Check if user already exists
        if (existsByUsername(username)) {
            throw new RuntimeException("Username is already taken!");
        }

        if (existsByEmail(email)) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);

        User savedUser = userRepository.save(user);
        log.info("New user created: {}", username);

        return savedUser;
    }

    /**
     * Updates an existing user's information.
     *
     * @param user The user entity to update
     * @return The updated user entity
     */
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Deletes a user account by ID.
     *
     * @param userId The ID of the user to delete
     */
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("User deleted with id: {}", userId);
    }

    /**
     * Retrieves the current user by username.
     * Used for authenticated operations.
     *
     * @param username The username of the current user
     * @return The user entity
     * @throws UsernameNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Updates a user's password.
     * Verifies the old password before allowing the change.
     *
     * @param username    The username of the user
     * @param oldPassword The current password for verification
     * @param newPassword The new password to set
     * @return The updated user entity
     * @throws RuntimeException if current password is incorrect
     */
    @Transactional
    public User updatePassword(String username, String oldPassword, String newPassword) {
        User user = getCurrentUser(username);

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    /**
     * Updates a user's profile information.
     * Validates that the new email is not already in use by another user.
     *
     * @param username  The username of the user
     * @param firstName The new first name
     * @param lastName  The new last name
     * @param email     The new email address
     * @return The updated user entity
     * @throws RuntimeException if email is already in use by another user
     */
    @Transactional
    public User updateProfile(String username, String firstName, String lastName, String email) {
        User user = getCurrentUser(username);

        // Check if email is already taken by another user
        if (!user.getEmail().equals(email) && existsByEmail(email)) {
            throw new RuntimeException("Email is already in use by another user");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        return userRepository.save(user);
    }
}
