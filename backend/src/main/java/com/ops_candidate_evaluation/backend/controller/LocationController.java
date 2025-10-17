package com.ops_candidate_evaluation.backend.controller;

import com.ops_candidate_evaluation.backend.model.Location;
import com.ops_candidate_evaluation.backend.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = "*")
public class LocationController {
    private final LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Location> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping
    public ResponseEntity<Location> create(@RequestBody Location l) {
        return ResponseEntity.ok(service.create(l));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> update(@PathVariable Long id, @RequestBody Location l) {
        return ResponseEntity.ok(service.update(id, l));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
