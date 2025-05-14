package com.medacare.backend.service;

import org.springframework.stereotype.Service;
import com.cloudinary.Cloudinary;
import com.medacare.backend.config.FixedVars;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryFileUploadService implements FileUploadService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        System.out.println("HERERRR");
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IOException("No file provided or file is empty");
        }
        String uploaded = "";
        try {
            uploaded = cloudinary.uploader()
                    .upload(multipartFile.getBytes(),
                            Map.of("public_id", UUID.randomUUID().toString(),
                                    "resource_type", "auto"))
                    .get("url")
                    .toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("File was not uploaded");
        }
        return uploaded;
    }

    public boolean fileIsNull(MultipartFile file) {
        return file == null;
    }

    public boolean fileIsEmpty(MultipartFile file) {
        return file.isEmpty();
    }

    public boolean fileIsValid(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    public boolean isValidDocument(MultipartFile file) {
        return file != null && !file.isEmpty() && FixedVars.ALLOWED_DOCUMENT_TYPES.contains(file.getContentType());
    }

    public boolean isValidPhoto(MultipartFile file) {
        return file != null && !file.isEmpty() && FixedVars.ALLOWED_IMAGE_TYPES.contains(file.getContentType());
    }
}