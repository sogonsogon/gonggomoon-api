package com.sogonsogon.gonggomoon.domain.strategy.infrastructure;

import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioStrategyJpaRepository
        extends JpaRepository<PortfolioStrategy, Long>, PortfolioStrategyRepository {
}
