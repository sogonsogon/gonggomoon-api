package com.sogonsogon.gonggomoon.domain.portfolioStrategy.infrastructure;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListQueryItem;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyDetailQueryResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PortfolioStrategyJpaRepository
        extends JpaRepository<PortfolioStrategy, Long>, PortfolioStrategyRepository {

    // COALESCE 함수로 NULL 값 처리
    @Query("""
        select new com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListQueryItem(
            ps.id,
            ps.publicId,
            p.publicId,
            pa.publicId,
            pa.title,
            ps.jobType,
            coalesce(i.name, '마스터'),
            ps.status,
            ps.createdAt
        )
        from PortfolioStrategy ps
        left join Post p on ps.postId = p.id
        left join PostAnalysis pa on ps.postAnalysisId = pa.id
        left join Industry i on ps.industryId = i.id
        where ps.userId = :userId
        order by ps.createdAt desc, ps.id desc
    """)
    List<PortfolioStrategyListQueryItem> findFirstPortfolioStrategyListByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("""
        select new com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListQueryItem(
            ps.id,
            ps.publicId,
            p.publicId,
            pa.publicId,
            pa.title,
            ps.jobType,
            coalesce(i.name, '마스터'),
            ps.status,
            ps.createdAt
        )
        from PortfolioStrategy ps
        left join Post p on ps.postId = p.id
        left join PostAnalysis pa on ps.postAnalysisId = pa.id
        left join Industry i on ps.industryId = i.id
        where ps.userId = :userId
          and (
            ps.createdAt < :cursorCreatedAt
            or (ps.createdAt = :cursorCreatedAt and ps.id < :cursorId)
          )
        order by ps.createdAt desc, ps.id desc
    """)
    List<PortfolioStrategyListQueryItem> findNextPortfolioStrategyListByUserId(
            @Param("userId") Long userId,
            @Param("cursorCreatedAt") Instant cursorCreatedAt,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("""
        select new com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyDetailQueryResult(
            ps,
            p.publicId,
            pa.publicId,
            pa.title
        )
        from PortfolioStrategy ps
        left join Post p on ps.postId = p.id
        left join PostAnalysis pa on ps.postAnalysisId = pa.id
        where ps.publicId = :publicId
          and ps.userId = :userId
    """)
    Optional<PortfolioStrategyDetailQueryResult> findPortfolioStrategyDetailByPublicIdAndUserId(
            @Param("publicId") UUID publicId,
            @Param("userId") Long userId
    );
}
