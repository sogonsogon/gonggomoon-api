package com.sogonsogon.gonggomoon.domain.experience.infrastructure;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;
import com.sogonsogon.gonggomoon.global.config.JpaAuditConfig;
import jakarta.persistence.EntityManager;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({ExperienceJpaRepositoryQueryCountTest.QueryCountTestConfig.class, JpaAuditConfig.class})
class ExperienceJpaRepositoryQueryCountTest {

    private static final Long USER_ID = 1L;
    private static final int TEST_DATA_COUNT = 12;
    private static final int WARMUP_COUNT = 3;
    private static final int MEASURE_COUNT = 10;
    private static final String TEST_COMMAND = "./gradlew test --tests \"com.sogonsogon.gonggomoon.domain.experience.infrastructure.ExperienceJpaRepositoryQueryCountTest\"";

    @Autowired
    private ExperienceJpaRepository experienceJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SelectQueryCounter selectQueryCounter;

    @Test
    void findAllByIdInAndUserId는_반복_단건조회보다_SELECT_호출을_줄인다() {
        List<Long> ids = saveExperiences();

        runWarmup(ids);

        QueryMeasure singleLookup = measureAverage(() -> findOneByOne(ids));
        QueryMeasure inLookup = measureAverage(() -> experienceJpaRepository.findAllByIdInAndUserId(ids, USER_ID));

        assertThat(singleLookup.resultSize()).isEqualTo(TEST_DATA_COUNT);
        assertThat(inLookup.resultSize()).isEqualTo(TEST_DATA_COUNT);
        assertThat(singleLookup.selectCount()).isEqualTo(TEST_DATA_COUNT);
        assertThat(inLookup.selectCount()).isEqualTo(1);

        printResult(singleLookup, inLookup);
    }

    private List<Long> saveExperiences() {
        List<Experience> experiences = new ArrayList<>();
        for (int i = 1; i <= TEST_DATA_COUNT; i++) {
            experiences.add(Experience.create(
                    USER_ID,
                    "경험 " + i,
                    ExperienceType.PROJECT,
                    "성능 검증용 경험 내용 " + i,
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 31)
            ));
        }

        List<Long> ids = experienceJpaRepository.saveAll(experiences).stream()
                .map(Experience::getId)
                .toList();

        entityManager.flush();
        entityManager.clear();
        selectQueryCounter.reset();

        return ids;
    }

    private void runWarmup(List<Long> ids) {
        for (int i = 0; i < WARMUP_COUNT; i++) {
            findOneByOne(ids);
            entityManager.clear();
            experienceJpaRepository.findAllByIdInAndUserId(ids, USER_ID);
            entityManager.clear();
        }
        selectQueryCounter.reset();
    }

    private QueryMeasure measureAverage(QueryOperation operation) {
        long totalNanos = 0L;
        int lastResultSize = 0;
        int lastSelectCount = 0;

        for (int i = 0; i < MEASURE_COUNT; i++) {
            entityManager.clear();
            selectQueryCounter.reset();

            long start = System.nanoTime();
            List<Experience> result = operation.execute();
            long elapsed = System.nanoTime() - start;

            totalNanos += elapsed;
            lastResultSize = result.size();
            lastSelectCount = selectQueryCounter.count();
        }

        return new QueryMeasure(lastSelectCount, totalNanos / (double) MEASURE_COUNT, lastResultSize);
    }

    private List<Experience> findOneByOne(List<Long> ids) {
        return ids.stream()
                .map(id -> experienceJpaRepository.findByIdAndUserId(id, USER_ID))
                .flatMap(Optional::stream)
                .toList();
    }

    private void printResult(QueryMeasure singleLookup, QueryMeasure inLookup) {
        System.out.printf("""

                [Experience IN 조회 성능 검증]
                - 비교 대상 로직: 반복 단건 조회(findByIdAndUserId N회) vs findAllByIdInAndUserId IN 조회
                - 기존 방식 SELECT 호출 횟수: %d회
                - 개선 방식 SELECT 호출 횟수: %d회
                - 기존 방식 평균 실행 시간: %.3f ms
                - 개선 방식 평균 실행 시간: %.3f ms
                - SELECT 호출 횟수 비교: SELECT %d회 -> SELECT %d회
                - 테스트 데이터 수: %d개
                - 실행한 테스트 명령어: %s
                - 포트폴리오에 쓸 수 있는 한 줄: Experience 다건 조회를 IN 쿼리로 개선해 N건 조회 시 SELECT 호출을 %d회에서 1회로 줄였고, 쿼리 카운터 기반 테스트로 검증했습니다.

                """,
                singleLookup.selectCount(),
                inLookup.selectCount(),
                singleLookup.averageMillis(),
                inLookup.averageMillis(),
                singleLookup.selectCount(),
                inLookup.selectCount(),
                TEST_DATA_COUNT,
                TEST_COMMAND,
                singleLookup.selectCount()
        );
    }

    @FunctionalInterface
    private interface QueryOperation {
        List<Experience> execute();
    }

    private record QueryMeasure(int selectCount, double averageNanos, int resultSize) {
        double averageMillis() {
            return averageNanos / 1_000_000.0;
        }
    }

    static class SelectQueryCounter implements StatementInspector {
        private final AtomicInteger count = new AtomicInteger();

        @Override
        public String inspect(String sql) {
            if (sql != null && sql.trim().toLowerCase(Locale.ROOT).startsWith("select")) {
                count.incrementAndGet();
            }
            return sql;
        }

        void reset() {
            count.set(0);
        }

        int count() {
            return count.get();
        }
    }

    @TestConfiguration
    static class QueryCountTestConfig {
        @Bean
        SelectQueryCounter selectQueryCounter() {
            return new SelectQueryCounter();
        }

        @Bean
        HibernatePropertiesCustomizer queryCountStatementInspector(SelectQueryCounter selectQueryCounter) {
            return properties -> properties.put("hibernate.session_factory.statement_inspector", selectQueryCounter);
        }
    }
}
