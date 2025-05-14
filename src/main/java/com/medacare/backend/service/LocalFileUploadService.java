package com.medacare.backend.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.medacare.backend.config.FixedVars;
import com.medacare.backend.dto.StandardResponse;

import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocalFileUploadService implements FileUploadService {

    @Value("${local.storage.path}")
    private String localStoragePath;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {

        try {
            String fileName = file.getOriginalFilename();
            file.transferTo(new File(localStoragePath + fileName));
            return localStoragePath + fileName;
        } catch (Exception ex) {
            throw new IOException("Unable to store file");
        }
    }
}
