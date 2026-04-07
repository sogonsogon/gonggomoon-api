package com.sogonsogon.gonggomoon.domain.file.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.file.api.request.UploadFileRequest;
import com.sogonsogon.gonggomoon.domain.file.api.response.UploadFileResponse;
import com.sogonsogon.gonggomoon.domain.file.api.response.UploadedFileListResponse;
import com.sogonsogon.gonggomoon.domain.file.application.FileAssetService;
import com.sogonsogon.gonggomoon.domain.file.application.result.UploadFileResult;
import com.sogonsogon.gonggomoon.domain.file.application.result.UploadedFileListResult;
import com.sogonsogon.gonggomoon.domain.file.domain.DocumentCategory;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileController {

    private final FileAssetService experienceImportService;

    /**
     * 파일을 업로드 합니다.
     * @param user
     * @param req
     * @param file
     * @return
     */
    @PostMapping(
            value = "/uploads/experiences",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BaseResponse<UploadFileResponse>> uploadFile(
            @AuthenticationPrincipal AccessUser user,
            @RequestPart("request") @Valid UploadFileRequest req,
            @RequestPart("file") MultipartFile file
    ) {
        UploadFileResult result = experienceImportService.uploadFile(
                user.getId(),
                req,
                file
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(UploadFileResponse.from(result)));
    }

    /**
     * 업로드된 파일 목록을 조회 합니다.
     */
    @GetMapping("/uploads/experiences")
    public ResponseEntity<BaseResponse<UploadedFileListResponse>> getUploadFileList(
            @AuthenticationPrincipal AccessUser user,
            @RequestParam(required = false) DocumentCategory documentCategory) {
        UploadedFileListResult result = experienceImportService.getFileList(user.getId(), documentCategory);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(UploadedFileListResponse.from(result)));
    }

    /**
     * 업로드된 파일을 삭제합니다.
     */
    @DeleteMapping("/uploads/experiences/{fileAssetId}")
    public ResponseEntity<BaseResponse<Void>> deleteFile(
            @AuthenticationPrincipal AccessUser user,
            @PathVariable("fileAssetId") Long fileAssetId) {
        experienceImportService.deleteFile(fileAssetId, user.getId());

        return ResponseEntity.ok(BaseResponse.success());
    }
}
