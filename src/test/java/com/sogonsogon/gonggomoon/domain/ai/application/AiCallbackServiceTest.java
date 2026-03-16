package com.sogonsogon.gonggomoon.domain.ai.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.BaseCallbackRequest;
import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.strategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AiCallbackServiceTest {

    @Mock
    private PortfolioStrategyRepository portfolioStrategyRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AiCallbackService aiCallbackService;

    private static final Long USER_ID = 1L;

    private static final Long INDUSTRY_ID = 1L;

    private static final Long STRATEGY_ID = 1L;

    @Nested
    @DisplayName("request")
    class requestTest {
        @Test
        @DisplayName("전략 결과 JSON 직렬화에 실패하면 RESULT_JSON_SERIALIZATION_FAILED 예외가 발생한다")
        void updatePortfolioStrategy_fail_whenResultJsonSerializationFails() throws Exception {
            // given
            BaseCallbackRequest request = mock(BaseCallbackRequest.class);

            PortfolioStrategy strategy = PortfolioStrategy.create(
                    USER_ID,
                    JobType.BACKEND,
                    INDUSTRY_ID,
                    2,
                    Instant.now(),
                    LocalDate.now(ZoneId.of("Asia/Seoul"))
            );

            Map<String, Object> result = new HashMap<>();
            result.put("portfolioStrategy", Map.of("title", "백엔드 전략", "summary", "요약"));

            JsonNode resultNode = new ObjectMapper().valueToTree(result);

            when(request.result()).thenReturn(resultNode);

            when(request.id()).thenReturn(STRATEGY_ID);
            when(request.userId()).thenReturn(USER_ID);

            when(portfolioStrategyRepository.findByIdAndUserId(STRATEGY_ID, USER_ID))
                    .thenReturn(Optional.of(strategy));

            when(objectMapper.writeValueAsString(any()))
                    .thenThrow(new JsonProcessingException("serialization failed") {});

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> aiCallbackService.updatePortfolioStrategy(request)
            );

            // then
            assertEquals(
                    PortfolioStrategyErrorCode.RESULT_JSON_SERIALIZATION_FAILED,
                    exception.getErrorCode()
            );

            verify(portfolioStrategyRepository).findByIdAndUserId(STRATEGY_ID, USER_ID);
            verify(objectMapper).writeValueAsString(any());
            verify(portfolioStrategyRepository, never()).save(any());
        }
    }
}
