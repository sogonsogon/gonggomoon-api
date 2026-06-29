package com.sogonsogon.gonggomoon.domain.file.application;

import com.sogonsogon.gonggomoon.domain.file.api.request.UploadFileRequest;
import com.sogonsogon.gonggomoon.domain.file.application.result.UploadFileResult;
import com.sogonsogon.gonggomoon.domain.file.application.result.UploadedFileListResult;
import com.sogonsogon.gonggomoon.domain.file.domain.DocumentCategory;
import com.sogonsogon.gonggomoon.domain.file.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.file.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.experience.error.FileAssetErrorCode;
import com.sogonsogon.gonggomoon.domain.file.port.FileStorage;
import com.sogonsogon.gonggomoon.global.file.FileKeyGenerator;
import com.sogonsogon.gonggomoon.global.config.MultipartProperties;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileAssetService {

    private final FileAssetRepository fileAssetRepository;
    private final MultipartProperties multipartProperties;
    private final FileStorage fileStorage;

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

        fileStorage.upload(fileKey, file);

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

        fileStorage.delete(fileAsset.getFileKey());
        fileAssetRepository.delete(fileAsset);
    }

    /**
     * 경험 추출용 임시 파일 정리 서비스
     * <p>
     * 추출 완료(콜백)·발행 실패·스케줄러 정리 등에서 호출되는 멱등 삭제 메서드.
     * 이미 삭제됐거나 존재하지 않는 파일은 건너뛰고, S3 삭제가 실패하더라도
     * 나머지 파일 정리와 FileAsset 행 삭제는 계속 진행한다.
     *
     * @param fileAssetIds 삭제할 FileAsset ID 목록
     */
    @Transactional
    public void deleteTemporaryFiles(List<Long> fileAssetIds) {
        if (fileAssetIds == null || fileAssetIds.isEmpty()) {
            return;
        }

        for (Long fileAssetId : fileAssetIds) {
            fileAssetRepository.findById(fileAssetId).ifPresent(fileAsset -> {
                try {
                    fileStorage.delete(fileAsset.getFileKey());
                } catch (RuntimeException exception) {
                    // S3 삭제 실패 시에도 행은 삭제한다. (드물게 S3 객체가 고아로 남을 수 있어 에러 로그로 추적)
                    log.error("임시 파일 S3 삭제 실패 fileAssetId={}, fileKey={}", fileAssetId, fileAsset.getFileKey(), exception);
                }
                fileAssetRepository.delete(fileAsset);
            });
        }
    }
}
