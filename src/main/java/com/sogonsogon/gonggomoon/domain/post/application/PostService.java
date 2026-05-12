package com.sogonsogon.gonggomoon.domain.post.application;

import com.sogonsogon.gonggomoon.domain.post.domain.PostRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.PostStatus;
import com.sogonsogon.gonggomoon.domain.post.dto.request.SearchPostRequest;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostResponse;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostsResponse;
import com.sogonsogon.gonggomoon.domain.post.error.PostErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Page<PostsResponse> searchPosts(SearchPostRequest request, Pageable pageable) {

        return postRepository.searchPosts(request.jobType(), PostStatus.PUBLISHED, request.title(), pageable);
    }

    public PostResponse getPost(Long id) {

        log.info("조회 상세 ID: " + id);

        PostResponse response = postRepository.getPost(id)
                .orElseThrow(() -> new BaseException(PostErrorCode.POST_NOT_PUBLISHED));

        if (response.status() != PostStatus.PUBLISHED) throw new BaseException(PostErrorCode.POST_NOT_PUBLISHED);

        return response;
    }
}
