package com.sogonsogon.gonggomoon.domain.ai.domain;

import java.util.List;
import java.util.Optional;

public interface PostAnalysisRepository {
    Optional<PostAnalysis> findByUrl(String url);

    <S extends PostAnalysis> List<S> saveAll(Iterable<S> entities);
}
