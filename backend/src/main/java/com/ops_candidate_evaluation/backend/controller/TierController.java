package com.ops_candidate_evaluation.backend.controller;

import com.ops_candidate_evaluation.backend.model.Tier;
import com.ops_candidate_evaluation.backend.service.TierService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tier")
@CrossOrigin(origins = "*")
public class TierController {
    private final TierService service;

    public TierController(TierService service) {
        this.service = service;
    }

    @GetMapping
    public List<Tier> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tier> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping
    public ResponseEntity<Tier> create(@RequestBody Tier t) {
        return ResponseEntity.ok(service.create(t));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tier> update(@PathVariable Long id, @RequestBody Tier t) {
        return ResponseEntity.ok(service.update(id, t));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
