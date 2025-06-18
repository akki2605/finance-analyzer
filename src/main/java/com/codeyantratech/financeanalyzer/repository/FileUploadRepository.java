package com.codeyantratech.financeanalyzer.repository;

import com.codeyantratech.financeanalyzer.model.FileUpload;
import com.codeyantratech.financeanalyzer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing FileUpload entities.
 * Provides methods for accessing and manipulating file upload records in the database.
 */
@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    
    /**
     * Finds all file uploads for a specific user, ordered by upload date in descending order.
     *
     * @param user The user whose file uploads to retrieve
     * @return List of FileUpload entities
     */
    List<FileUpload> findByUserOrderByUploadDateDesc(User user);

    /**
     * Finds a specific file upload by ID and user.
     * Used to ensure users can only access their own files.
     *
     * @param id   The ID of the file upload
     * @param user The user who should own the file
     * @return Optional containing the FileUpload if found
     */
    Optional<FileUpload> findByIdAndUser(Long id, User user);
} 