package com.codeyantratech.financeanalyzer.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FileUploadResponse {
    private Long id;
    private String filename;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private Boolean processed;
    private Integer recordsCount;
    private String status;
    private String errorDetails;
} 