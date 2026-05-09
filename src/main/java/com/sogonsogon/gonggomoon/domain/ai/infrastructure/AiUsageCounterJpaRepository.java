package com.sogonsogon.gonggomoon.domain.ai.infrastructure;

import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageCounter;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageCounterRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AiUsageCounterJpaRepository extends JpaRepository<AiUsageCounter, Long>,
    AiUsageCounterRepository {

    Optional<AiUsageCounter> findByUserIdAndUsageTypeAndWeekStartDate(
        Long userId,
        AiUsageType usageType,
        LocalDate weekStartDate
    );

    @Override
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update AiUsageCounter counter
        set counter.usageCount = counter.usageCount + 1
        where counter.userId = :userId
          and counter.usageType = :usageType
          and counter.weekStartDate = :weekStartDate
          and counter.usageCount < :limitCount
    """)
    int incrementIfBelowLimit(
        @Param("userId") Long userId,
        @Param("usageType") AiUsageType usageType,
        @Param("weekStartDate") LocalDate weekStartDate,
        @Param("limitCount") int limitCount
    );

    @Override
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update AiUsageCounter counter
        set counter.usageCount = counter.usageCount - 1
        where counter.userId = :userId
          and counter.usageType = :usageType
          and counter.weekStartDate = :weekStartDate
          and counter.usageCount > 0
    """)
    int decrementIfPositive(
        @Param("userId") Long userId,
        @Param("usageType") AiUsageType usageType,
        @Param("weekStartDate") LocalDate weekStartDate
    );
}
