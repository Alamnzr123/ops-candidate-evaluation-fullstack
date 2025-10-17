package com.ops_candidate_evaluation.backend.repository;

import com.ops_candidate_evaluation.backend.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCode(String code);
}
