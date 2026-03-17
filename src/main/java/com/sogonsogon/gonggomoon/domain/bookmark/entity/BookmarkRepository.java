package com.sogonsogon.gonggomoon.domain.bookmark.entity;

import com.sogonsogon.gonggomoon.domain.bookmark.dto.BookmarkListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookmarkRepository {

    Bookmark save(Bookmark bookmark);

    boolean existsBookmarkByPostIdAndUserId(Long postId, Long userId);

    Page<BookmarkListResponse> findBookmarksByUserId(Long userId,
                                                     Pageable pageable);

    Optional<Bookmark> findById(Long bookmarkId);

    void delete(Bookmark bookmark);
}
