package com.sogonsogon.gonggomoon.domain.post.infrastructure;

import com.sogonsogon.gonggomoon.domain.post.domain.PostAnalysis;
import com.sogonsogon.gonggomoon.domain.post.domain.PostAnalysisRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostAnalysisJpaRepository extends JpaRepository<PostAnalysis, Long>, PostAnalysisRepository {
}
