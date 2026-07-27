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
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Hidden
@Deprecated
@Tag(name = "파일", description = "파일 업로드, 목록 조회, 삭제 API")
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class FileV2Controller {
    private final FileAssetService fileAssetService;

    /**
     * 파일을 업로드 합니다.
     */
    @Operation(summary = "파일 업로드", description = "현재 로그인한 사용자가 멀티파트 요청으로 파일을 업로드합니다.")
    @PostMapping(
            value = "/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BaseResponse<UploadFileResponse>> uploadFile(
            @AuthenticationPrincipal AccessUser user,
            @RequestPart("request") @Valid UploadFileRequest req,
            @RequestPart("file") MultipartFile file
    ) {
        UploadFileResult result = fileAssetService.uploadFile(
                user.getId(),
                req,
                file
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(UploadFileResponse.from(result)));
    }

    /**
     * 업로드된 파일 목록을 조회 합니다.
     */
    @Operation(summary = "파일 목록 조회", description = "현재 로그인한 사용자가 업로드한 파일 목록을 문서 카테고리로 선택 필터링하여 조회합니다.")
    @GetMapping("/files")
    public ResponseEntity<BaseResponse<UploadedFileListResponse>> getUploadFileList(
            @AuthenticationPrincipal AccessUser user,
            @RequestParam(required = false) DocumentCategory documentCategory) {
        UploadedFileListResult result = fileAssetService.getFileList(user.getId(), documentCategory);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(UploadedFileListResponse.from(result)));
    }

    /**
     * 업로드된 파일을 삭제합니다.
     */
    @Operation(summary = "파일 삭제", description = "현재 로그인한 사용자가 업로드한 지정한 파일을 삭제합니다.")
    @DeleteMapping("/files/{fileAssetId}")
    public ResponseEntity<BaseResponse<Void>> deleteFile(
            @AuthenticationPrincipal AccessUser user,
            @PathVariable("fileAssetId") Long fileAssetId) {
        fileAssetService.deleteFile(fileAssetId, user.getId());

        return ResponseEntity.ok(BaseResponse.success());
    }
}
