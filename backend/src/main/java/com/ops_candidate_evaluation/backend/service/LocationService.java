package com.ops_candidate_evaluation.backend.service;

import com.ops_candidate_evaluation.backend.model.Location;
import com.ops_candidate_evaluation.backend.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    private final LocationRepository repo;

    public LocationService(LocationRepository repo) {
        this.repo = repo;
    }

    public List<Location> list() {
        return repo.findAll();
    }

    public Location get(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Location create(Location l) {
        return repo.save(l);
    }

    public Location update(Long id, Location l) {
        l.setId(id);
        return repo.save(l);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
