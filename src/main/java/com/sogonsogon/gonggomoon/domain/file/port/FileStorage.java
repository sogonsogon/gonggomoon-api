package com.sogonsogon.gonggomoon.domain.file.port;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    void upload(String key, MultipartFile file);

    void delete(String fileKey);
}
