package com.sogonsogon.gonggomoon.domain.file.application;

import com.sogonsogon.gonggomoon.domain.file.api.request.UploadFileRequest;
import com.sogonsogon.gonggomoon.domain.file.application.result.UploadFileResult;
import com.sogonsogon.gonggomoon.domain.file.application.result.UploadedFileListResult;
import com.sogonsogon.gonggomoon.domain.file.domain.DocumentCategory;
import com.sogonsogon.gonggomoon.domain.file.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.file.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.experience.error.FileAssetErrorCode;
import com.sogonsogon.gonggomoon.global.file.FileKeyGenerator;
import com.sogonsogon.gonggomoon.global.config.MultipartProperties;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import com.sogonsogon.gonggomoon.domain.file.infrastructure.s3.S3FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FileAssetService {

    private final FileAssetRepository fileAssetRepository;
    private final MultipartProperties multipartProperties;
    private final S3FileStorage s3Uploader;

    /**
     * 파일 업로드 서비스
     * @param userId
     * @param req
     * @param file
     * @return
     */
    public UploadFileResult uploadFile(Long userId, UploadFileRequest req, MultipartFile file) {

        validateFile(file);

        String fileKey = FileKeyGenerator.generate(file.getOriginalFilename());

        s3Uploader.upload(fileKey, file);

        FileAsset fileAsset = FileAsset.create(
                userId,
                req.category(),
                file.getOriginalFilename(),
                fileKey,
                file.getSize());

        fileAssetRepository.save(fileAsset);

        return UploadFileResult.from(fileAsset);
    }

    /**
     * MultipartFile 검증
     * @param file
     */
    private void validateFile (MultipartFile file) {
        if (file == null) {
            throw new BaseException(FileAssetErrorCode.FILE_REQUIRED);
        }

        if (file.isEmpty()) {
            throw new BaseException(FileAssetErrorCode.EMPTY_FILE_NOT_ALLOWED);
        }

        if (file.getOriginalFilename().isBlank()) {
            throw new BaseException(FileAssetErrorCode.INVALID_FILE_NAME);
        }

        if (file.getSize() > multipartProperties.getMaxFileSize().toBytes()) {
            throw new BaseException(FileAssetErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    /**
     * 파일 목록 조회 서비스
     * @param userId
     * @return
     */
    public UploadedFileListResult getFileList(Long userId, DocumentCategory documentCategory) {
        List<FileAsset> fileAssets = (documentCategory == null)
                ? fileAssetRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                : fileAssetRepository.findAllByUserIdAndCategoryOrderByCreatedAtDesc(userId, documentCategory);

        return UploadedFileListResult.from(fileAssets);
    }


    /**
     * 파일 삭제 서비스
     */
    public void deleteFile(Long fileAssetId, Long userId) {
        FileAsset fileAsset = fileAssetRepository.findByIdAndUserId(fileAssetId, userId)
                .orElseThrow(() -> new BaseException(FileAssetErrorCode.NOT_FOUND));

        s3Uploader.delete(fileAsset.getFileKey());
        fileAssetRepository.delete(fileAsset);
    }
}
