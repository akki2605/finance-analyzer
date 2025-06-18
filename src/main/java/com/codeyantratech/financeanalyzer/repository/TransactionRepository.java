package com.codeyantratech.financeanalyzer.repository;

import com.codeyantratech.financeanalyzer.model.Transaction;
import com.codeyantratech.financeanalyzer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Transaction entities.
 * Provides methods for accessing and manipulating transaction records in the database.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Finds all transactions for a specific user, ordered by transaction date in descending order.
     *
     * @param user The user whose transactions to retrieve
     * @return List of Transaction entities
     */
    List<Transaction> findByUserOrderByTransactionDateDesc(User user);

    /**
     * Finds a specific transaction by ID and user.
     * Used to ensure users can only access their own transactions.
     *
     * @param id   The ID of the transaction
     * @param user The user who should own the transaction
     * @return Optional containing the Transaction if found
     */
    Optional<Transaction> findByIdAndUser(Long id, User user);
} 