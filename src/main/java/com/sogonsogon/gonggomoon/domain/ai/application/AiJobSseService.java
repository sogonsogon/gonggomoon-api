package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.AiFunctionStatusResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class AiJobSseService {

    private static final long SSE_TIMEOUT_MILLIS = 30 * 60 * 1000L;

    private final Map<AiJobKey, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(Long userId, AiFunctions type, Long id) {
        AiJobKey key = new AiJobKey(userId, type, id);
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MILLIS);

        emitter.onCompletion(() -> remove(key, emitter));
        emitter.onTimeout(() -> remove(key, emitter));
        emitter.onError(e -> remove(key, emitter));

        emitters.computeIfAbsent(key, unused -> ConcurrentHashMap.newKeySet())
            .add(emitter);

        return emitter;
    }

    public void send(Long userId, AiFunctionStatusResponse response) {
        AiJobKey key = new AiJobKey(userId, response.type(), response.id());
        Set<SseEmitter> jobEmitters = emitters.get(key);

        if (jobEmitters == null || jobEmitters.isEmpty()) {
            return;
        }

        jobEmitters.forEach(emitter -> sendToEmitter(key, emitter, response));
    }

    public void complete(Long userId, AiFunctions type, Long id) {
        AiJobKey key = new AiJobKey(userId, type, id);
        Set<SseEmitter> jobEmitters = emitters.remove(key);

        if (jobEmitters != null) {
            jobEmitters.forEach(SseEmitter::complete);
        }
    }

    private void sendToEmitter(AiJobKey key, SseEmitter emitter, AiFunctionStatusResponse response) {
        try {
            emitter.send(SseEmitter.event()
                .name("ai-job-status")
                .data(response));
        } catch (IOException | IllegalStateException e) {
            remove(key, emitter);
            emitter.completeWithError(e);
        }
    }

    private void remove(AiJobKey key, SseEmitter emitter) {
        Set<SseEmitter> jobEmitters = emitters.get(key);

        if (jobEmitters == null) {
            return;
        }

        jobEmitters.remove(emitter);

        if (jobEmitters.isEmpty()) {
            emitters.remove(key, jobEmitters);
        }
    }

    private record AiJobKey(Long userId, AiFunctions type, Long id) {
    }
}
