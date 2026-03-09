package com.sogonsogon.gonggomoon.global.file;

import com.sogonsogon.gonggomoon.domain.experience.error.FileAssetErrorCode;
import com.sogonsogon.gonggomoon.global.config.S3Properties;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public void upload(String key, MultipartFile file) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new BaseException(FileAssetErrorCode.FILE_STREAM_READ_FAILED);
        } catch (Exception e) {
            throw new BaseException(FileAssetErrorCode.S3_UPLOAD_FAILED);
        }
    }

    public void delete(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new BaseException(FileAssetErrorCode.FILE_DELETE_FAILED);
        }
    }
}
