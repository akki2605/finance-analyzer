package com.codeyantratech.financeanalyzer.controller;

import com.codeyantratech.financeanalyzer.dto.ApiResponse;
import com.codeyantratech.financeanalyzer.dto.FileUploadResponse;
import com.codeyantratech.financeanalyzer.model.FileUpload;
import com.codeyantratech.financeanalyzer.security.UserPrincipal;
import com.codeyantratech.financeanalyzer.service.CsvProcessingService;
import com.codeyantratech.financeanalyzer.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for handling file upload operations.
 * Provides endpoints for uploading CSV files, retrieving upload history, and file details.
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private CsvProcessingService csvProcessingService;

    /**
     * Handles file upload requests.
     * Validates that the file is not empty and is a CSV file.
     * Processes the file content and creates transaction records.
     *
     * @param file          The uploaded file (must be CSV)
     * @param userPrincipal The authenticated user's principal
     * @return ResponseEntity with success/error message
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Please select a file to upload"));
        }

        // Validate file type
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Please upload a CSV file"));
        }

        try {
            csvProcessingService.processTransactionCsv(file, userPrincipal.getUsername());
            return ResponseEntity.ok(ApiResponse.success("File uploaded and processed successfully"));
        } catch (Exception e) {
            log.error("Error processing file upload: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error processing file: " + e.getMessage()));
        }
    }

    /**
     * Retrieves the file upload history for the authenticated user.
     *
     * @param userPrincipal The authenticated user's principal
     * @return List of file upload records
     */
    @GetMapping
    public ResponseEntity<List<FileUploadResponse>> getUserFiles(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<FileUpload> files = fileUploadService.getUserFiles(userPrincipal.getUsername());
        List<FileUploadResponse> response = files.stream()
                .map(this::toFileUploadResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves details for a specific file upload.
     *
     * @param fileId        The ID of the file upload record
     * @param userPrincipal The authenticated user's principal
     * @return The requested file upload record if found and owned by the user
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<FileUploadResponse> getFileDetails(
            @PathVariable Long fileId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        FileUpload file = fileUploadService.getFileById(fileId, userPrincipal.getUsername());
        return ResponseEntity.ok(toFileUploadResponse(file));
    }

    private FileUploadResponse toFileUploadResponse(FileUpload fileUpload) {
        return FileUploadResponse.builder()
                .id(fileUpload.getId())
                .filename(fileUpload.getFilename())
                .fileSize(fileUpload.getFileSize())
                .uploadDate(fileUpload.getUploadDate())
                .processed(fileUpload.getProcessed())
                .recordsCount(fileUpload.getRecordsCount())
                .status(fileUpload.getStatus())
                .errorDetails(fileUpload.getErrorDetails())
                .build();
    }
} 