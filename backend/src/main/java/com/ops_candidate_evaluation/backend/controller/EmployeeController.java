package com.ops_candidate_evaluation.backend.controller;

import com.ops_candidate_evaluation.backend.model.Employee;
import com.ops_candidate_evaluation.backend.service.EmployeeService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Employee> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Employee e) {
        try {
            Employee created = service.create(e);
            return ResponseEntity.ok(created);
        } catch (DataIntegrityViolationException ex) {
            // return readable message for unique/constraint violations
            String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage()
                    : ex.getMessage();
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "error", "Data integrity violation",
                    "message", detail));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Employee e) {
        return ResponseEntity.ok(service.update(id, e));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
