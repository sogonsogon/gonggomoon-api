package com.sogonsogon.gonggomoon.domain.file.application;

import com.sogonsogon.gonggomoon.domain.file.api.request.UploadFileRequest;
import com.sogonsogon.gonggomoon.domain.file.application.result.UploadFileResult;
import com.sogonsogon.gonggomoon.domain.file.application.result.UploadedFileListResult;
import com.sogonsogon.gonggomoon.domain.file.application.result.UploadedFileListResultItem;
import com.sogonsogon.gonggomoon.domain.file.domain.DocumentCategory;
import com.sogonsogon.gonggomoon.domain.file.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.file.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.experience.error.FileAssetErrorCode;
import com.sogonsogon.gonggomoon.global.config.MultipartProperties;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import com.sogonsogon.gonggomoon.global.file.S3Uploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FileAssetServiceTest {
    @Mock
    private FileAssetRepository fileAssetRepository;

    @Mock
    private MultipartProperties multipartProperties;

    @Mock
    private S3Uploader s3Uploader;

    @InjectMocks
    private FileAssetService fileAssetService;

    private static final Long USER_ID = 1L;
    private static final DataSize MAX_FILE_SIZE = DataSize.ofMegabytes(10);

    @Nested
    @DisplayName("uploadFile")
    class UploadFileTest {

        @Test
        @DisplayName("정상 파일이면 S3 업로드 후 FileAsset을 저장하고 결과를 반환한다")
        void uploadFile_success() throws Exception {
            // given
            when(multipartProperties.getMaxFileSize()).thenReturn(MAX_FILE_SIZE);
            UploadFileRequest req = createRequest();
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "resume.pdf",
                    "application/pdf",
                    "dummy-content".getBytes() // 빈 파일 방지
            );

            when(fileAssetRepository.save(any(FileAsset.class))).thenAnswer(invocation -> {
                FileAsset saved = invocation.getArgument(0);
                setField(saved, "id", 100L);
                return saved;
            });

            // when
            UploadFileResult result = fileAssetService.uploadFile(USER_ID, req, file);

            // then
            assertNotNull(result);
            assertEquals(100L, result.fileAssetId());

            verify(s3Uploader).upload(anyString(), same(file));
            verify(fileAssetRepository).save(any(FileAsset.class));
        }

        @Test
        @DisplayName("파일이 null이면 FILE_REQUIRED 예외가 발생한다")
        void uploadFile_fail_whenFileIsNull() {
            // given
            UploadFileRequest req = createRequest();

            // when
            BaseException ex = assertThrows(
                    BaseException.class,
                    () -> fileAssetService.uploadFile(USER_ID, req, null)
            );

            // then
            assertEquals(FileAssetErrorCode.FILE_REQUIRED, ex.getErrorCode());
            verify(s3Uploader, never()).upload(anyString(), any());
            verify(fileAssetRepository, never()).save(any());
        }

        @Test
        @DisplayName("빈 파일이면 EMPTY_FILE_NOT_ALLOWED 예외가 발생한다")
        void uploadFile_fail_whenFileIsEmpty() {
            // given
            UploadFileRequest req = createRequest();
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file",
                    "resume.pdf",
                    "application/pdf",
                    new byte[0]
            );

            // when
            BaseException ex = assertThrows(
                    BaseException.class,
                    () -> fileAssetService.uploadFile(USER_ID, req, emptyFile)
            );

            // then
            assertEquals(FileAssetErrorCode.EMPTY_FILE_NOT_ALLOWED, ex.getErrorCode());
            verify(s3Uploader, never()).upload(anyString(), any());
            verify(fileAssetRepository, never()).save(any());
        }

        @Test
        @DisplayName("파일명이 blank면 INVALID_FILE_NAME 예외가 발생한다")
        void uploadFile_fail_whenOriginalFilenameIsBlank() {
            // given
            UploadFileRequest req = createRequest();
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    " ",
                    "application/pdf",
                    "dummy-content".getBytes()
            );

            // when
            BaseException ex = assertThrows(
                    BaseException.class,
                    () -> fileAssetService.uploadFile(USER_ID, req, file)
            );

            // then
            assertEquals(FileAssetErrorCode.INVALID_FILE_NAME, ex.getErrorCode());
            verify(s3Uploader, never()).upload(anyString(), any());
            verify(fileAssetRepository, never()).save(any());
        }

        @Test
        @DisplayName("최대 파일 크기를 초과하면 FILE_SIZE_EXCEEDED 예외가 발생한다")
        void uploadFile_fail_whenFileSizeExceeded() {
            // given
            when(multipartProperties.getMaxFileSize()).thenReturn(MAX_FILE_SIZE);

            UploadFileRequest req = createRequest();
            byte[] oversized = new byte[(int) MAX_FILE_SIZE.toBytes() + 1];
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "resume.pdf",
                    "application/pdf",
                    oversized
            );

            // when
            BaseException ex = assertThrows(
                    BaseException.class,
                    () -> fileAssetService.uploadFile(USER_ID, req, file)
            );

            // then
            assertEquals(FileAssetErrorCode.FILE_SIZE_EXCEEDED, ex.getErrorCode());
            verify(s3Uploader, never()).upload(anyString(), any());
            verify(fileAssetRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getFileList")
    class GetFileListTest {

        @Test
        @DisplayName("documentCategory가 null이면 사용자의 업로드 파일 목록을 생성일시 내림차순 조회 결과로 반환한다")
        void getFileList_withoutCategory_success() throws Exception {
            // given
            Instant createdAt1 = Instant.parse("2026-03-08T10:15:30Z");
            Instant createdAt2 = Instant.parse("2026-03-07T09:00:00Z");

            FileAsset file1 = createFileAsset(
                    100L,
                    USER_ID,
                    DocumentCategory.RESUME,
                    "resume.pdf",
                    1024L,
                    createdAt1
            );

            FileAsset file2 = createFileAsset(
                    101L,
                    USER_ID,
                    DocumentCategory.PORTFOLIO,
                    "portfolio.pdf",
                    2048L,
                    createdAt2
            );

            when(fileAssetRepository.findAllByUserIdOrderByCreatedAtDesc(USER_ID))
                    .thenReturn(List.of(file1, file2));

            // when
            UploadedFileListResult result = fileAssetService.getFileList(USER_ID, null);

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.contents()).hasSize(2);

            UploadedFileListResultItem first = result.contents().get(0);
            assertThat(first.fileAssetId()).isEqualTo(100L);
            assertThat(first.category()).isEqualTo(DocumentCategory.RESUME);
            assertThat(first.originalFileName()).isEqualTo("resume.pdf");
            assertThat(first.sizeBytes()).isEqualTo(1024L);
            assertThat(first.createdAt()).isEqualTo(createdAt1);

            UploadedFileListResultItem second = result.contents().get(1);
            assertThat(second.fileAssetId()).isEqualTo(101L);
            assertThat(second.category()).isEqualTo(DocumentCategory.PORTFOLIO);
            assertThat(second.originalFileName()).isEqualTo("portfolio.pdf");
            assertThat(second.sizeBytes()).isEqualTo(2048L);
            assertThat(second.createdAt()).isEqualTo(createdAt2);

            verify(fileAssetRepository).findAllByUserIdOrderByCreatedAtDesc(USER_ID);
            verify(fileAssetRepository, never())
                    .findAllByUserIdAndCategoryOrderByCreatedAtDesc(anyLong(), any());
        }

        @Test
        @DisplayName("documentCategory가 있으면 해당 카테고리의 업로드 파일 목록만 생성일시 내림차순으로 반환한다")
        void getFileList_withCategory_success() throws Exception {
            // given
            Instant createdAt1 = Instant.parse("2026-03-08T10:15:30Z");
            Instant createdAt2 = Instant.parse("2026-03-07T09:00:00Z");

            FileAsset file1 = createFileAsset(
                    101L,
                    USER_ID,
                    DocumentCategory.PORTFOLIO,
                    "portfolio-1.pdf",
                    2048L,
                    createdAt1
            );

            FileAsset file2 = createFileAsset(
                    102L,
                    USER_ID,
                    DocumentCategory.PORTFOLIO,
                    "portfolio-2.pdf",
                    4096L,
                    createdAt2
            );

            when(fileAssetRepository.findAllByUserIdAndCategoryOrderByCreatedAtDesc(
                    USER_ID, DocumentCategory.PORTFOLIO))
                    .thenReturn(List.of(file1, file2));

            // when
            UploadedFileListResult result = fileAssetService.getFileList(USER_ID, DocumentCategory.PORTFOLIO);

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.contents()).hasSize(2);

            UploadedFileListResultItem first = result.contents().get(0);
            assertThat(first.fileAssetId()).isEqualTo(101L);
            assertThat(first.category()).isEqualTo(DocumentCategory.PORTFOLIO);
            assertThat(first.originalFileName()).isEqualTo("portfolio-1.pdf");
            assertThat(first.sizeBytes()).isEqualTo(2048L);
            assertThat(first.createdAt()).isEqualTo(createdAt1);

            UploadedFileListResultItem second = result.contents().get(1);
            assertThat(second.fileAssetId()).isEqualTo(102L);
            assertThat(second.category()).isEqualTo(DocumentCategory.PORTFOLIO);
            assertThat(second.originalFileName()).isEqualTo("portfolio-2.pdf");
            assertThat(second.sizeBytes()).isEqualTo(4096L);
            assertThat(second.createdAt()).isEqualTo(createdAt2);

            verify(fileAssetRepository).findAllByUserIdAndCategoryOrderByCreatedAtDesc(
                    USER_ID, DocumentCategory.PORTFOLIO);
            verify(fileAssetRepository, never()).findAllByUserIdOrderByCreatedAtDesc(anyLong());
        }

        @Test
        @DisplayName("업로드한 파일이 없으면 빈 목록을 반환한다")
        void getFileList_empty() {
            // given
            when(fileAssetRepository.findAllByUserIdAndCategoryOrderByCreatedAtDesc(
                    USER_ID, DocumentCategory.OTHER))
                    .thenReturn(List.of());

            // when
            UploadedFileListResult result = fileAssetService.getFileList(USER_ID, DocumentCategory.OTHER);

            // then
            assertThat(result).isNotNull();
            assertThat(result.totalCount()).isZero();
            assertThat(result.contents()).isEmpty();

            verify(fileAssetRepository).findAllByUserIdAndCategoryOrderByCreatedAtDesc(
                    USER_ID, DocumentCategory.OTHER);
            verify(fileAssetRepository, never()).findAllByUserIdOrderByCreatedAtDesc(anyLong());
        }
    }

    @Nested
    @DisplayName("deleteFile")
    class DeleteFileTest {

        @Test
        @DisplayName("존재하는 파일이면 S3와 DB에서 삭제한다.")
        void deleteFile_success() throws Exception{
            // given
            Long fileAssetId = 100L;
            FileAsset fileAsset = createFileAsset(
                    fileAssetId,
                    USER_ID,
                    DocumentCategory.RESUME,
                    "resume.pdf",
                    1024L,
                    Instant.parse("2026-03-08T10:15:30Z")
            );

            when(fileAssetRepository.findByIdAndUserId(fileAssetId, USER_ID))
                    .thenReturn(java.util.Optional.of(fileAsset));

            // when
            fileAssetService.deleteFile(fileAssetId, USER_ID);

            // then
            verify(fileAssetRepository).findByIdAndUserId(fileAssetId, USER_ID);
            verify(s3Uploader).delete(fileAsset.getFileKey());
            verify(fileAssetRepository).delete(fileAsset);
        }

        @Test
        @DisplayName("사용자 소유의 파일이 없으면 NOT_FOUND 예외가 발생한다")
        void deleteFile_fail_whenFileAssetNotFound() {
            // given
            Long fileAssetId = 100L;

            when(fileAssetRepository.findByIdAndUserId(fileAssetId, USER_ID))
                    .thenReturn(java.util.Optional.empty());

            // when
            BaseException ex = assertThrows(
                    BaseException.class,
                    () -> fileAssetService.deleteFile(fileAssetId, USER_ID)
            );

            // then
            assertEquals(FileAssetErrorCode.NOT_FOUND, ex.getErrorCode());
            verify(fileAssetRepository).findByIdAndUserId(fileAssetId, USER_ID);
            verify(s3Uploader, never()).delete((anyString()));
            verify(fileAssetRepository, never()).delete(any(FileAsset.class));
        }
    }

    private FileAsset createFileAsset(
            Long id,
            Long userId,
            DocumentCategory category,
            String originalFileName,
            Long sizeBytes,
            Instant createdAt
    ) throws Exception {
        FileAsset fileAsset = FileAsset.create(
                userId,
                category,
                originalFileName,
                "test-file-key",
                sizeBytes
        );

        setField(fileAsset, "id", id);
        setField(fileAsset, "createdAt", createdAt);

        return fileAsset;
    }

    private UploadFileRequest createRequest() {
        return new UploadFileRequest(DocumentCategory.values()[0]);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}
