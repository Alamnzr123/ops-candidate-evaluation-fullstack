package com.ops_candidate_evaluation.backend.service;

import com.ops_candidate_evaluation.backend.model.Employee;
import com.ops_candidate_evaluation.backend.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public List<Employee> list() {
        return repo.findAll();
    }

    public Employee get(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Employee create(Employee e) {
        return repo.save(e);
    }

    public Employee update(Long id, Employee e) {
        e.setId(id);
        return repo.save(e);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
