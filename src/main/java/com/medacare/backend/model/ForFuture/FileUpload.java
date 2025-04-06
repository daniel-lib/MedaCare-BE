package com.medacare.backend.model.ForFuture;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.medacare.backend.model.User;

public class FileUpload {

    private long id;
    private User uploader; //patient, doctor,...
    private String filePath;    // byte[] type to be referenced by AI analysis
    
    @CreationTimestamp
    private LocalDateTime uploadDate;
    @UpdateTimestamp
    private LocalDateTime updateDate;
}
