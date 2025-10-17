package com.ops_candidate_evaluation.backend.repository;

import com.ops_candidate_evaluation.backend.model.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TierRepository extends JpaRepository<Tier, Long> {
    Optional<Tier> findByCode(String code);
}
