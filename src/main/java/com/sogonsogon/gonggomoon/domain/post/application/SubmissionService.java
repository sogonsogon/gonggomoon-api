package com.sogonsogon.gonggomoon.domain.post.application;

import com.sogonsogon.gonggomoon.domain.post.dto.request.SubmitPostRequest;
import com.sogonsogon.gonggomoon.domain.post.domain.Platform;
import com.sogonsogon.gonggomoon.domain.post.domain.PlatformRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.PostRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.PostSubmission;
import com.sogonsogon.gonggomoon.domain.post.domain.PostSubmissionRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.PostSubmissionStatus;
import com.sogonsogon.gonggomoon.domain.post.error.PlatformErrorCode;
import com.sogonsogon.gonggomoon.domain.post.error.SubmissionErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class SubmissionService {

    private final PostSubmissionRepository postSubmissionRepository;
    private final PlatformRepository platformRepository;
    private final PostRepository postRepository;

    public SubmissionService(PostSubmissionRepository postSubmissionRepository, PlatformRepository platformRepository, PostRepository postRepository) {
        this.postSubmissionRepository = postSubmissionRepository;
        this.platformRepository = platformRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public void submitPost(SubmitPostRequest request, Long userId) {

        // 해당 플랫폼 존재 하는지 검증
        Platform platform = platformRepository.findById(request.platformId())
                .orElseThrow(() -> new BaseException(PlatformErrorCode.PLATFORM_NOT_FOUND));

        // baseUrl 검증
        try {
            URI uri = new URI(request.postUrl());
            String host = uri.getHost();

            if (host == null ||
                    (!host.equalsIgnoreCase(platform.getBaseUrl()) && !host.toLowerCase().endsWith("." + platform.getBaseUrl().toLowerCase()))) {
                throw new BaseException(SubmissionErrorCode.URL_PLATFORM_MISMATCH);
            }
        } catch (URISyntaxException e) {
            throw new BaseException(SubmissionErrorCode.INVALID_URL_FORMAT);
        }
        // 해당 url을 가지고 있는 공고 있는지 확인
        if (postRepository.existsByUrl(request.postUrl())) throw new BaseException(SubmissionErrorCode.DUPLICATE_URL);

        // 해당 유저가 동일한 요청을 했는지 검증
        if (postSubmissionRepository.existsByUrlAndUserIdAndStatus(request.postUrl(), userId, PostSubmissionStatus.PENDING))
            throw new BaseException(SubmissionErrorCode.DUPLICATE_SUBMISSION);

        PostSubmission postSubmission = PostSubmission.create(
                request.postUrl(),
                userId,
                request.platformId()
        );

        postSubmissionRepository.save(postSubmission);
    }
}
