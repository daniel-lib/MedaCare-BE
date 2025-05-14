package com.medacare.backend.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileUpload implements Serializable{

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


    // private Long fileOwner; //change to OBJECT...Postibly User type

    @CreationTimestamp
    private OffsetDateTime uploadDate;
    @UpdateTimestamp
    private OffsetDateTime updateDate;
}
