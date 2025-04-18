package com.medacare.backend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUpload {

    private long id;
    private Long uploader; // patient, doctor, admin
    private String filePath;
    private String fileId;
    private String fileURL;
    private String fileName;
    private String fileType; // pdf, image, video, audio, etc.
    private String fileDescription;
    private String fileSize; // in bytes
    private String fileStatus; // pending, approved, rejected
    private String fileCategory; // medical report, prescription, etc.
    private String fileAccess; // public, private, shared
    private String fileVisibility; // visible, hidden
    private String fileSharing; // shared, not shared
    private String fileSharingType; // public, private, group

    private RoleEnum fileOwnerRole; // patient, doctor,...


    private Long fileOwner; // patient, doctor,...

    @CreationTimestamp
    private LocalDateTime uploadDate;
    @UpdateTimestamp
    private LocalDateTime updateDate;
}
