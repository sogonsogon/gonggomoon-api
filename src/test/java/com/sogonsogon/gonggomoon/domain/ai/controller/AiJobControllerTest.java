package com.sogonsogon.gonggomoon.domain.ai.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.AiFunctionStatusRequest;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.UUID;

class AiJobControllerTest {

    private final AiService aiService = mock(AiService.class);
    private final AiJobController controller = new AiJobController(aiService);

    @Test
    void subscribeAlwaysReturnsSseResponse() {
        AccessUser user = mock(AccessUser.class);
        SseEmitter emitter = new SseEmitter();
        UUID jobId = UUID.randomUUID();
        AiFunctionStatusRequest request =
            new AiFunctionStatusRequest(AiFunctions.POST_ANALYSIS, jobId);

        when(user.getId()).thenReturn(1L);
        when(aiService.subscribe(1L, request)).thenReturn(emitter);

        ResponseEntity<SseEmitter> response = controller.subscribeAiJobStatus(
            user,
            AiFunctions.POST_ANALYSIS,
            jobId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(emitter);
        verify(aiService).subscribe(1L, request);
    }
}
