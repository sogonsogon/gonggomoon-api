package com.sogonsogon.gonggomoon.domain.bookmark.application;

import com.sogonsogon.gonggomoon.domain.bookmark.dto.BookmarkListResponse;
import com.sogonsogon.gonggomoon.domain.bookmark.dto.CreateBookmarkRequest;
import com.sogonsogon.gonggomoon.domain.bookmark.entity.Bookmark;
import com.sogonsogon.gonggomoon.domain.bookmark.entity.BookmarkRepository;
import com.sogonsogon.gonggomoon.domain.bookmark.error.BookmarkErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import com.sogonsogon.gonggomoon.global.error.GlobalErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @Transactional
    public void createBookmark(CreateBookmarkRequest request, Long userId) {

        if (bookmarkRepository.existsBookmarkByPostIdAndUserId(request.postId(), userId)) {
            throw new BaseException(BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS);
        }

        Bookmark bookmark = Bookmark.create(userId, request.postId());

        bookmarkRepository.save(bookmark);
    }

    public Page<BookmarkListResponse> getBookmarks(Long userId, Pageable pageable) {

        return bookmarkRepository.findBookmarksByUserId(userId, pageable);
    }

    public void deleteBookmark(Long bookmarkId, Long userId) {

        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new BaseException(BookmarkErrorCode.BOOKMARK_NOT_FOUND));

        if (!bookmark.getUserId().equals(userId)) throw new BaseException(GlobalErrorCode.INVALID_INPUT_VALUE);

        bookmarkRepository.delete(bookmark);
    }
}
