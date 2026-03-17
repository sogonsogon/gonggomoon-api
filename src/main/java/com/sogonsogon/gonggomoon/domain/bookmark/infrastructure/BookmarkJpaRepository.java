package com.sogonsogon.gonggomoon.domain.bookmark.infrastructure;

import com.sogonsogon.gonggomoon.domain.bookmark.dto.BookmarkListResponse;
import com.sogonsogon.gonggomoon.domain.bookmark.entity.Bookmark;
import com.sogonsogon.gonggomoon.domain.bookmark.entity.BookmarkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkJpaRepository extends JpaRepository<Bookmark, Long>, BookmarkRepository {

    @Query(
            value = """
                SELECT new com.sogonsogon.gonggomoon.domain.bookmark.dto.BookmarkListResponse(
                    b.id,
                    p.id,
                    p.title,
                    c.name,
                    p.status,
                    p.startedAt,
                    p.expiredAt,
                    b.createdAt
                )
                FROM Bookmark b
                JOIN Post p ON p.id = b.postId
                JOIN Company c ON c.id = p.companyId
                WHERE b.userId = :id
        """,
            countQuery = """
            SELECT COUNT(b)
            FROM Bookmark b
            WHERE b.userId = :id
"""
    )
    Page<BookmarkListResponse> findBookmarksByUserId(@Param("id") Long userId,
                                                     Pageable pageable);
}
