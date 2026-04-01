package com.sogonsogon.gonggomoon.domain.experience.domain;

import com.sogonsogon.gonggomoon.domain.experience.error.FileAssetErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import com.sogonsogon.gonggomoon.global.file.FileAssetPolicy;
import com.sogonsogon.gonggomoon.global.validation.ValidationUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * 파일 자산의 상태를 관리합니다.
 */
@Builder
@Getter
@Entity
@Table(name = "file_asset")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class FileAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_category", nullable = false)
    private DocumentCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileAssetStatus status; // 파일 저장 상태

    @Column(name = "original_file_name", nullable = false, length = FileAssetPolicy.MAX_ORIGINAL_FILE_NAME_LENGTH)
    private String originalFileName; // 원본 파일명

    @Column(name = "file_key", nullable = false, length = FileAssetPolicy.MAX_FILE_KEY_LENGTH)
    private String fileKey; // 저장 경로

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes; // 용량

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static FileAsset create(
            Long userId,
            DocumentCategory category,
            String originalFileName,
            String fileKey,
            Long sizeBytes
    ) {
        ValidationUtils.requireNonNull(userId, FileAssetErrorCode.USER_ID_REQUIRED);
        ValidationUtils.requireNonNull(category, FileAssetErrorCode.CATEGORY_REQUIRED);
        ValidationUtils.requireText(originalFileName, FileAssetErrorCode.ORIGINAL_FILE_NAME_REQUIRED);
        ValidationUtils.requireText(fileKey, FileAssetErrorCode.FILE_KEY_REQUIRED);
        validateFileSize(sizeBytes);

        return FileAsset.builder()
                .userId(userId)
                .category(category)
                .status(FileAssetStatus.UPLOADED)
                .originalFileName(originalFileName)
                .fileKey(fileKey)
                .sizeBytes(sizeBytes)
                .build();
    }

    private static void validateFileSize(Long sizeBytes) {
        if (sizeBytes == null || sizeBytes <= 0 ) {
            throw new BaseException(FileAssetErrorCode.INVALID_FILE_SIZE);
        }
    }
}
