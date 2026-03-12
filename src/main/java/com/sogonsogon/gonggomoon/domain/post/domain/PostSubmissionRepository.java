package com.sogonsogon.gonggomoon.domain.post.domain;

public interface PostSubmissionRepository {

    boolean existsByUrlAndUserIdAndStatus(String url, Long id, PostSubmissionStatus status);

    PostSubmission save(PostSubmission postSubmission);
}
