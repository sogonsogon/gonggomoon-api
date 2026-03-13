package com.sogonsogon.gonggomoon.domain.strategy.infrastructure;

import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyListResultItem;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioStrategyJpaRepository
        extends JpaRepository<PortfolioStrategy, Long>, PortfolioStrategyRepository {

    @Query("""
        select new com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyListResultItem(
            ps.id,
            ps.jobType,
            i.name,
            ps.createdAt
        )
        from PortfolioStrategy ps
        left join Industry i on ps.industryId = i.id
        where ps.userId = :userId
        order by ps.createdAt desc
    """)
    List<PortfolioStrategyListResultItem> findPortfolioStrategyListByUserId(@Param("userId") Long userId);
}
