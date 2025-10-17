package com.ops_candidate_evaluation.backend.service;

import com.ops_candidate_evaluation.backend.model.Tier;
import com.ops_candidate_evaluation.backend.repository.TierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TierService {
    private final TierRepository repo;

    public TierService(TierRepository repo) {
        this.repo = repo;
    }

    public List<Tier> list() {
        return repo.findAll();
    }

    public Tier get(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Tier create(Tier t) {
        return repo.save(t);
    }

    public Tier update(Long id, Tier t) {
        t.setId(id);
        return repo.save(t);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
