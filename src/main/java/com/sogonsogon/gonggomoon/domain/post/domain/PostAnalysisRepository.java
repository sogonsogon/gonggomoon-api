package com.sogonsogon.gonggomoon.domain.post.domain;

import java.util.Optional;

public interface PostAnalysisRepository {
    Optional<PostAnalysis> findByUrl(String url);

    void save(PostAnalysis postAnalysis);
}
