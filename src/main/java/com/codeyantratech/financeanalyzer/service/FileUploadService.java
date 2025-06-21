package com.codeyantratech.financeanalyzer.service;

import com.codeyantratech.financeanalyzer.model.FileUpload;
import com.codeyantratech.financeanalyzer.model.User;
import com.codeyantratech.financeanalyzer.repository.FileUploadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class responsible for managing file upload operations.
 * Handles saving file metadata, retrieving file history, and updating file processing status.
 */
@Slf4j
@Service
public class FileUploadService {

    @Autowired
    private FileUploadRepository fileUploadRepository;
    
    @Autowired
    private UserService userService;

    /**
     * Saves metadata for an uploaded file.
     *
     * @param file     The uploaded MultipartFile
     * @param username The username of the user uploading the file
     * @return FileUpload entity containing the file metadata
     * @throws IOException if there's an error handling the file
     */
    @Transactional
    public FileUpload saveFile(MultipartFile file, String username) throws IOException {
        User user = userService.getCurrentUser(username);
        
        // Create new file upload record
        FileUpload fileUpload = new FileUpload(
            user,
            file.getOriginalFilename(),
            file.getSize()
        );
        
        return fileUploadRepository.save(fileUpload);
    }

    /**
     * Retrieves all files uploaded by a specific user.
     *
     * @param username The username of the user
     * @return List of FileUpload entities ordered by upload date descending
     */
    @Transactional(readOnly = true)
    public List<FileUpload> getUserFiles(String username) {
        User user = userService.getCurrentUser(username);
        return fileUploadRepository.findByUserOrderByUploadDateDesc(user);
    }

    /**
     * Retrieves a specific file upload record by ID for a user.
     *
     * @param fileId   The ID of the file upload record
     * @param username The username of the user requesting the file
     * @return FileUpload entity if found and owned by the user
     * @throws RuntimeException if the file is not found or user doesn't have access
     */
    @Transactional(readOnly = true)
    public FileUpload getFileById(Long fileId, String username) {
        User user = userService.getCurrentUser(username);
        return fileUploadRepository.findByIdAndUser(fileId, user)
            .orElseThrow(() -> new RuntimeException("File not found or access denied"));
    }

    /**
     * Updates a file upload record to mark it as processed.
     *
     * @param fileUpload   The FileUpload entity to update
     * @param recordsCount The number of records successfully processed from the file
     */
    @Transactional
    public void markFileAsProcessed(FileUpload fileUpload, int recordsCount) {
        fileUpload.setProcessed(true);
        fileUpload.setRecordsCount(recordsCount);
        fileUploadRepository.save(fileUpload);
    }
} 