package com.codeyantratech.financeanalyzer.service;

import com.codeyantratech.financeanalyzer.enums.TransactionSource;
import com.codeyantratech.financeanalyzer.enums.TransactionType;
import com.codeyantratech.financeanalyzer.model.FileUpload;
import com.codeyantratech.financeanalyzer.model.Transaction;
import com.codeyantratech.financeanalyzer.model.User;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for processing CSV files containing transaction data.
 * Handles parsing CSV content, validating data, and creating transaction records.
 */
@Slf4j
@Service
public class CsvProcessingService {

    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private UserService userService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Processes a CSV file containing transaction data.
     * The file should have the following columns: date, amount, type, description
     * The first row is expected to be a header row and will be skipped.
     *
     * @param file     The uploaded CSV file
     * @param username The username of the user uploading the file
     * @throws RuntimeException if there's an error processing the file
     */
    @Transactional
    public void processTransactionCsv(MultipartFile file, String username) {
        FileUpload fileUpload = null;
        try {
            // Save file upload record
            fileUpload = fileUploadService.saveFile(file, username);
            fileUpload.setStatus("PROCESSING");
            fileUpload = fileUploadService.updateFileUpload(fileUpload);

            User user = userService.getCurrentUser(username);

            // Process CSV file
            List<Transaction> transactions = parseTransactions(file, user);
            
            // Save transactions
            transactions.forEach(transactionService::saveTransaction);
            
            // Mark file as processed successfully
            fileUpload.setStatus("SUCCESS");
            fileUpload.setProcessed(true);
            fileUpload.setRecordsCount(transactions.size());
            fileUploadService.updateFileUpload(fileUpload);
            
            log.info("Successfully processed {} transactions from file: {}", 
                    transactions.size(), file.getOriginalFilename());
            
        } catch (Exception e) {
            log.error("Error processing CSV file: {}", e.getMessage());
            
            // Update file upload record with error details
            if (fileUpload != null) {
                fileUpload.setStatus("FAILED");
                fileUpload.setErrorDetails(e.getMessage());
                fileUploadService.updateFileUpload(fileUpload);
            }
            
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage());
        }
    }

    /**
     * Parses the CSV file and converts each row into a Transaction object.
     * Skips the header row and any invalid records.
     *
     * @param file The CSV file to parse
     * @param user The user who owns these transactions
     * @return List of Transaction objects created from the CSV data
     * @throws IOException   if there's an error reading the file
     * @throws CsvException if there's an error parsing the CSV format
     */
    private List<Transaction> parseTransactions(MultipartFile file, User user) throws IOException, CsvException {
        List<Transaction> transactions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withSkipLines(1) // Skip header row
                     .build()) {

            List<String[]> records = csvReader.readAll();
            
            for (String[] record : records) {
                try {
                    Transaction transaction = createTransactionFromRecord(record, user);
                    transactions.add(transaction);
                } catch (Exception e) {
                    log.warn("Skipping invalid record: {}", String.join(",", record));
                }
            }
        }
        
        return transactions;
    }

    /**
     * Creates a Transaction object from a CSV record.
     * Expected format: date,amount,type,description
     *
     * @param record Array of strings containing the CSV record data
     * @param user   The user who owns the transaction
     * @return Transaction object created from the record data
     * @throws IllegalArgumentException if the record format is invalid
     * @throws DateTimeParseException  if the date format is invalid
     */
    private Transaction createTransactionFromRecord(String[] record, User user) {
        if (record.length < 4) {
            throw new IllegalArgumentException("Invalid record format");
        }

        // Expected CSV format: date,amount,type,description
        LocalDate date = LocalDate.parse(record[0].trim(), DATE_FORMATTER);
        BigDecimal amount = new BigDecimal(record[1].trim());
        TransactionType type = TransactionType.valueOf(record[2].trim().toUpperCase());
        String description = record[3].trim();

        return Transaction.builder()
                .user(user)
                .transactionDate(date)
                .amount(amount)
                .transactionType(type)
                .description(description)
                .source(TransactionSource.CSV_UPLOAD)
                .build();
    }
} 