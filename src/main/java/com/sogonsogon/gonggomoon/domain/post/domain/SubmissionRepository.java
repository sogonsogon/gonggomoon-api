package com.sogonsogon.gonggomoon.domain.post.domain;

public interface SubmissionRepository {

    boolean existsByUrlAndUserIdAndStatus(String url, Long id, SubmissionStatus status);

    Submission save(Submission submission);
}
