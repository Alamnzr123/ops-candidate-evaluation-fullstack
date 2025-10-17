package com.ops_candidate_evaluation.backend.config;

import com.ops_candidate_evaluation.backend.model.ApiCallHistory;
import com.ops_candidate_evaluation.backend.repository.ApiCallHistoryRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Component
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private final ApiCallHistoryRepository repo;

    public ApiLoggingInterceptor(ApiCallHistoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) {
        ApiCallHistory h = new ApiCallHistory();
        h.setTimestamp(Instant.now());
        h.setMethod(request.getMethod());
        h.setPath(request.getRequestURI());
        h.setStatus(response.getStatus());
        // user identification: check header or session, fallback null
        String user = request.getHeader("X-User-Id");
        h.setUserIdentifier(user);

        // save async to avoid blocking response thread
        CompletableFuture.runAsync(() -> {
            try {
                repo.save(h);
            } catch (Exception ignore) {
            }
        });
    }
}
