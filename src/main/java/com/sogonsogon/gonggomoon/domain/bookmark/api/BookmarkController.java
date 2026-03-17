package com.sogonsogon.gonggomoon.domain.bookmark.api;

import com.sogonsogon.gonggomoon.domain.bookmark.application.BookmarkService;
import com.sogonsogon.gonggomoon.domain.bookmark.dto.BookmarkListResponse;
import com.sogonsogon.gonggomoon.domain.bookmark.dto.CreateBookmarkRequest;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    // 북마크 생성
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> createBookmark(@RequestBody @Valid CreateBookmarkRequest request,
                                                             @AuthenticationPrincipal UserDetails details) {

        bookmarkService.createBookmark(request, Long.valueOf(details.getUsername()));

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success());
    }

    // 북마크 목록 조회
    @GetMapping
    public ResponseEntity<BaseResponse<BaseResponse.PageResponse<BookmarkListResponse>>> getBookmarks(@AuthenticationPrincipal UserDetails details,
                                                                                                      Pageable pageable) {

        Page<BookmarkListResponse> page = bookmarkService.getBookmarks(Long.valueOf(details.getUsername()), pageable);

        return ResponseEntity.ok(BaseResponse.success(
                BaseResponse.PageResponse.<BookmarkListResponse>builder()
                        .content(page.getContent())
                        .pageInfo(BaseResponse.PageInfo.builder()
                                .currentPage(page.getNumber())
                                .totalPages(page.getTotalPages())
                                .totalElements(page.getTotalElements())
                                .hasNext(page.hasNext())
                                .build())
                        .build())
        );
    }

    // 북마크 삭제
    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<BaseResponse<Void>> deleteBookmark(@PathVariable Long bookmarkId) {

        bookmarkService.deleteBookmark(bookmarkId);

        return ResponseEntity.ok(BaseResponse.success());
    }
}
