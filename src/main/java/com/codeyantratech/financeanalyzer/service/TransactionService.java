package com.codeyantratech.financeanalyzer.service;

import com.codeyantratech.financeanalyzer.model.Transaction;
import com.codeyantratech.financeanalyzer.model.User;
import com.codeyantratech.financeanalyzer.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class responsible for managing financial transactions.
 * Handles CRUD operations for transactions and ensures user-specific data access.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;

    /**
     * Saves a new transaction or updates an existing one.
     *
     * @param transaction The transaction to save
     * @return The saved transaction with updated information
     */
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * Retrieves all transactions for a specific user.
     * Results are ordered by transaction date in descending order.
     *
     * @param username The username of the user
     * @return List of transactions belonging to the user
     */
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactions(String username) {
        User user = userService.getCurrentUser(username);
        return transactionRepository.findByUserOrderByTransactionDateDesc(user);
    }

    /**
     * Retrieves a specific transaction by ID for a user.
     * Ensures that users can only access their own transactions.
     *
     * @param transactionId The ID of the transaction to retrieve
     * @param username     The username of the requesting user
     * @return The requested transaction if found and owned by the user
     * @throws RuntimeException if the transaction is not found or user doesn't have access
     */
    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long transactionId, String username) {
        User user = userService.getCurrentUser(username);
        return transactionRepository.findByIdAndUser(transactionId, user)
            .orElseThrow(() -> new RuntimeException("Transaction not found or access denied"));
    }

    /**
     * Deletes a specific transaction.
     * Ensures that users can only delete their own transactions.
     *
     * @param transactionId The ID of the transaction to delete
     * @param username     The username of the requesting user
     * @throws RuntimeException if the transaction is not found or user doesn't have access
     */
    @Transactional
    public void deleteTransaction(Long transactionId, String username) {
        Transaction transaction = getTransactionById(transactionId, username);
        transactionRepository.delete(transaction);
    }
} 