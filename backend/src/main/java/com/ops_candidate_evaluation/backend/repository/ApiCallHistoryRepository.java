package com.ops_candidate_evaluation.backend.repository;

import com.ops_candidate_evaluation.backend.model.ApiCallHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiCallHistoryRepository extends JpaRepository<ApiCallHistory, Long> {
}
