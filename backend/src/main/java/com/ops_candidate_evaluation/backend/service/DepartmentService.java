package com.ops_candidate_evaluation.backend.service;

import com.ops_candidate_evaluation.backend.model.Department;
import com.ops_candidate_evaluation.backend.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentRepository repo;

    public DepartmentService(DepartmentRepository repo) {
        this.repo = repo;
    }

    public List<Department> list() {
        return repo.findAll();
    }

    public Department get(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Department create(Department d) {
        return repo.save(d);
    }

    public Department update(Long id, Department d) {
        d.setId(id);
        return repo.save(d);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
