package com.sogonsogon.gonggomoon.domain.post.infrastructure;

import com.sogonsogon.gonggomoon.domain.post.domain.Post;
import com.sogonsogon.gonggomoon.domain.post.domain.PostRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.PostStatus;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostResponse;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostsResponse;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostJpaRepository extends JpaRepository<Post, Long>, PostRepository {


    @Query(
            value = """
                SELECT new com.sogonsogon.gonggomoon.domain.post.dto.response.PostsResponse(
                    p.id,
                    c.id,
                    c.name,
                    pl.name,
                    p.title,
                    p.experienceLevel,
                    p.jobType,
                    p.startedAt,
                    p.expiredAt
                )
                FROM Post p
                JOIN Company c ON c.id = p.companyId
                LEFT JOIN Platform pl ON pl.id = p.platformId
                WHERE (:jobType IS NULL OR p.jobType = :jobType)
                AND (:title IS NULL OR p.title LIKE %:title%)
                AND p.status = :status
        """,
        countQuery = """
            SELECT COUNT(p)
            FROM Post p
            WHERE (:jobType IS NULL OR p.jobType = :jobType)
            AND (:title IS NULL OR p.title LIKE %:title%)
            AND p.status = :status
        """
    )
    Page<PostsResponse> searchPosts(
            @Param("jobType") JobType jobType,
            @Param("status") PostStatus status,
            @Param("title") String title,
            Pageable pageable
            );

    @Query(
            value = """
                SELECT new com.sogonsogon.gonggomoon.domain.post.dto.response.PostResponse(
                    p.id,
                    c.id,
                    i.id,
                    c.name,
                    i.name,
                    p.title,
                    p.url,
                    p.experienceLevel,
                    p.originalContent,
                    p.analyzedContent,
                    p.jobType,
                    p.status,
                    p.startedAt,
                    p.expiredAt
                )
                FROM Post p
                JOIN Company c ON c.id = p.companyId
                JOIN Industry i ON i.id = c.industryId
                WHERE p.id = :id
        """
    )
    Optional<PostResponse> getPost(
            @Param("id") Long id
    );
}
