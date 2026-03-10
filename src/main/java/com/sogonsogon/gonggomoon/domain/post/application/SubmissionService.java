package com.sogonsogon.gonggomoon.domain.post.application;

import com.sogonsogon.gonggomoon.domain.post.api.dto.request.SubmitPostRequest;
import com.sogonsogon.gonggomoon.domain.post.domain.Platform;
import com.sogonsogon.gonggomoon.domain.post.domain.PlatformRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.PostRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.Submission;
import com.sogonsogon.gonggomoon.domain.post.domain.SubmissionRepository;
import com.sogonsogon.gonggomoon.domain.post.domain.SubmissionStatus;
import com.sogonsogon.gonggomoon.domain.post.error.PlatformErrorCode;
import com.sogonsogon.gonggomoon.domain.post.error.SubmissionErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final PlatformRepository platformRepository;
    private final PostRepository postRepository;

    public SubmissionService(SubmissionRepository submissionRepository, PlatformRepository platformRepository, PostRepository postRepository) {
        this.submissionRepository = submissionRepository;
        this.platformRepository = platformRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public void submitPost(SubmitPostRequest request, Long userId) {

        // 해당 플랫폼 존재 하는지 검증
        Platform platform = platformRepository.findById(request.platformId())
                .orElseThrow(() -> new BaseException(PlatformErrorCode.PLATFORM_NOT_FOUND));

        // baseUrl 검증
        if (!request.requestUrl().startsWith(platform.getBaseUrl())) throw new BaseException(SubmissionErrorCode.URL_PLATFORM_MISMATCH);

        // 해당 url을 가지고 있는 공고 있는지 확인
        if (postRepository.existsByUrl(request.requestUrl())) throw new BaseException(SubmissionErrorCode.DUPLICATE_URL);

        // 해당 유저가 동일한 요청을 했는지 검증
        if (submissionRepository.existsByUrlAndUserIdAndStatus(request.requestUrl(), userId, SubmissionStatus.PENDING))
            throw new BaseException(SubmissionErrorCode.DUPLICATE_SUBMISSION);

        Submission submission = Submission.create(
                request.requestUrl(),
                userId,
                request.platformId()
        );

        submissionRepository.save(submission);
    }
}
