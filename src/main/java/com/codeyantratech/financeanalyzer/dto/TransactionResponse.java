package com.codeyantratech.financeanalyzer.dto;

import com.codeyantratech.financeanalyzer.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private TransactionType transactionType;
    private String description;
    private String categoryName;
    private Long categoryId;
} 