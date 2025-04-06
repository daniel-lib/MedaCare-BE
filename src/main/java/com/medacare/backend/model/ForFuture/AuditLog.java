package com.medacare.backend.model.ForFuture;

import java.time.LocalDateTime;
 
import org.hibernate.annotations.CreationTimestamp;

import com.medacare.backend.model.User;

public class AuditLog { //Logs system activity for security

    private long id;
    private String action;
    private User performedBy;
    @CreationTimestamp
    private LocalDateTime performedOn;
}
