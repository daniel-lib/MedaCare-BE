package com.medacare.backend.model.helper;



import java.io.Serializable;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.medacare.backend.model.Institution;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity
public class InstitutionFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Long uploader; // inst. admin ref
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


    @ManyToOne()
    @JoinColumn(name = "file_owner_institution")
    private Institution fileOwner;

    @CreationTimestamp
    private OffsetDateTime uploadDate;
    @UpdateTimestamp
    private OffsetDateTime updateDate;
}