package com.sogonsogon.gonggomoon.domain.post.domain;

import java.util.Optional;

public interface PostAnalysisRepository {
    Optional<PostAnalysis> findByPostUrl(String postUrl);

    PostAnalysis save(PostAnalysis postAnalysis);
}
