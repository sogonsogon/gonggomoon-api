package com.sogonsogon.gonggomoon.domain.ai.infrastructure;

import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysis;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysisRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostAnalysisJpaRepository extends JpaRepository<PostAnalysis, Long>, PostAnalysisRepository {
}
