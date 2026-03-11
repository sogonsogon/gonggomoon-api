package com.sogonsogon.gonggomoon.domain.post.domain;

import com.sogonsogon.gonggomoon.domain.post.dto.response.PostResponse;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostsResponse;
import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepository {

    boolean existsByUrl(String url);

    Page<PostsResponse> searchPosts(JobType jobType, PostStatus status, String title, Pageable pageable);

    Optional<PostResponse> getPost(Long id);
}
