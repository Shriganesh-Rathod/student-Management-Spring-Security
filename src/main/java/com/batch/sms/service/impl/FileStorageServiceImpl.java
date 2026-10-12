package com.batch.sms.service.impl;

import com.batch.sms.exception.FileStorageException;
import com.batch.sms.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    private final Path uploadLocation;

    public FileStorageServiceImpl(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create upload directory: " + uploadDir, ex);
        }
    }

    @Override
    public String store(MultipartFile file, String ownerIdPrefix) {
        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }

        // ownerId + random UUID keeps names unique even for repeated uploads
        // of the same filename by the same or different students.
        String storedFilename = ownerIdPrefix + "_" + UUID.randomUUID() + extension;

        try (InputStream inputStream = file.getInputStream()) {
            Path target = this.uploadLocation.resolve(storedFilename);
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file {} for owner {}", storedFilename, ownerIdPrefix);
            // Just the filename: WebConfig maps /images/** straight onto the
            // upload directory, so the entity only needs to remember the name.
            return storedFilename;
        } catch (IOException ex) {
            log.error("Failed to store file for owner {}", ownerIdPrefix, ex);
            throw new FileStorageException("Failed to store file " + originalFilename, ex);
        }
    }
}
