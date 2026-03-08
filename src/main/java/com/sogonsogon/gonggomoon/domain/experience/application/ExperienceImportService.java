package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.experience.api.request.ImportExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ImportExperienceResult;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.experience.error.FileAssetErrorCode;
import com.sogonsogon.gonggomoon.global.file.FileKeyGenerator;
import com.sogonsogon.gonggomoon.global.config.MultipartProperties;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import com.sogonsogon.gonggomoon.global.file.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class ExperienceImportService {

    private final FileAssetRepository fileAssetRepository;
    private final MultipartProperties multipartProperties;
    private final S3Uploader s3Uploader;

    /**
     * 파일 업로드 서비스
     * @param userId
     * @param req
     * @param file
     * @return
     */
    public ImportExperienceResult uploadFile(Long userId, ImportExperienceRequest req, MultipartFile file) {

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

        return ImportExperienceResult.from(fileAsset);
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
}
