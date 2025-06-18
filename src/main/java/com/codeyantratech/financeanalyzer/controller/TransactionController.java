package com.codeyantratech.financeanalyzer.controller;

import com.codeyantratech.financeanalyzer.dto.ApiResponse;
import com.codeyantratech.financeanalyzer.dto.TransactionRequest;
import com.codeyantratech.financeanalyzer.dto.TransactionResponse;
import com.codeyantratech.financeanalyzer.model.Category;
import com.codeyantratech.financeanalyzer.model.Transaction;
import com.codeyantratech.financeanalyzer.model.User;
import com.codeyantratech.financeanalyzer.security.UserPrincipal;
import com.codeyantratech.financeanalyzer.service.TransactionService;
import com.codeyantratech.financeanalyzer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    /**
     * List all transactions for the authenticated user
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Transaction> transactions = transactionService.getUserTransactions(userPrincipal.getUsername());
        List<TransactionResponse> response = transactions.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Get a single transaction by ID
     */
    @GetMapping("/transaction/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Transaction transaction = transactionService.getTransactionById(id, userPrincipal.getUsername());
        return ResponseEntity.ok(toResponse(transaction));
    }

    /**
     * Create a new transaction
     */
    @PostMapping("/transaction")
    public ResponseEntity<ApiResponse> createTransaction(@Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.getCurrentUser(userPrincipal.getUsername());
        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(request.getAmount())
                .transactionDate(request.getTransactionDate())
                .transactionType(request.getTransactionType())
                .description(request.getDescription())
                .build();
        // TODO: Set category if provided
        Transaction saved = transactionService.saveTransaction(transaction);
        return ResponseEntity.ok(ApiResponse.success("Transaction created", toResponse(saved)));
    }

    /**
     * Update an existing transaction
     */
    @PutMapping("/transaction/{id}")
    public ResponseEntity<ApiResponse> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Transaction transaction = transactionService.getTransactionById(id, userPrincipal.getUsername());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setDescription(request.getDescription());
        // TODO: Set category if provided
        Transaction updated = transactionService.saveTransaction(transaction);
        return ResponseEntity.ok(ApiResponse.success("Transaction updated", toResponse(updated)));
    }

    /**
     * Delete a transaction
     */
    @DeleteMapping("/transaction/{id}")
    public ResponseEntity<ApiResponse> deleteTransaction(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        transactionService.deleteTransaction(id, userPrincipal.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted"));
    }

    // Helper to convert Transaction to TransactionResponse
    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .amount(t.getAmount())
                .transactionDate(t.getTransactionDate())
                .transactionType(t.getTransactionType())
                .description(t.getDescription())
                .categoryId(t.getCategory() != null ? t.getCategory().getId() : null)
                .categoryName(t.getCategory() != null ? t.getCategory().getName() : null)
                .build();
    }
} 