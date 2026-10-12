package com.batch.sms.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Stores the file on disk and returns the path to save on the entity.
     * ownerIdPrefix (e.g. the student's id) keeps filenames unique and
     * traceable without needing a separate lookup.
     */
    String store(MultipartFile file, String ownerIdPrefix);
}
