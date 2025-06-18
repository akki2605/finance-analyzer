package com.codeyantratech.financeanalyzer.service;

import com.codeyantratech.financeanalyzer.model.FileUpload;
import com.codeyantratech.financeanalyzer.model.User;
import com.codeyantratech.financeanalyzer.repository.FileUploadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service class responsible for managing file upload operations.
 * Handles saving file metadata, retrieving file history, and updating file processing status.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileUploadRepository fileUploadRepository;
    private final UserService userService;

    /**
     * Saves metadata for an uploaded file.
     */
    @Transactional
    public FileUpload saveFile(MultipartFile file, String username) throws IOException {
        User user = userService.getCurrentUser(username);
        FileUpload fileUpload = new FileUpload(user, file.getOriginalFilename(), file.getSize());
        return fileUploadRepository.save(fileUpload);
    }

    /**
     * Updates an existing file upload record.
     */
    @Transactional
    public FileUpload updateFileUpload(FileUpload fileUpload) {
        return fileUploadRepository.save(fileUpload);
    }

    /**
     * Retrieves all files uploaded by a specific user.
     */
    @Transactional(readOnly = true)
    public List<FileUpload> getUserFiles(String username) {
        User user = userService.getCurrentUser(username);
        return fileUploadRepository.findByUserOrderByUploadDateDesc(user);
    }

    /**
     * Retrieves a specific file upload record by ID for a user.
     */
    @Transactional(readOnly = true)
    public FileUpload getFileById(Long fileId, String username) {
        User user = userService.getCurrentUser(username);
        return fileUploadRepository.findByIdAndUser(fileId, user)
            .orElseThrow(() -> new RuntimeException("File not found or access denied"));
    }

    /**
     * Updates a file upload record to mark it as processed.
     */
    @Transactional
    public void markFileAsProcessed(FileUpload fileUpload, int recordsCount) {
        fileUpload.setProcessed(true);
        fileUpload.setRecordsCount(recordsCount);
        fileUpload.setStatus("SUCCESS");
        fileUploadRepository.save(fileUpload);
    }
} 