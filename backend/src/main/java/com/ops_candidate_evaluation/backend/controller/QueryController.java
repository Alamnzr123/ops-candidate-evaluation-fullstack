package com.ops_candidate_evaluation.backend.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/query")
@CrossOrigin(origins = "*") // temporary for dev; prefer specific origin in production
public class QueryController {

    private final JdbcTemplate jdbc;

    public QueryController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Query 1 - cumulative salary (correlated subquery)
    @GetMapping("/q1")
    public List<Map<String, Object>> query1() {
        String sql = ""
                + "SELECT e.dept_code AS dept_code, e.emp_no AS emp_no, e.name AS emp_name, "
                + "  (SELECT SUM(x.salary) FROM employee x "
                + "    WHERE x.dept_code = e.dept_code AND x.emp_no <= e.emp_no) AS cumulative_salary "
                + "FROM employee e "
                + "ORDER BY e.dept_code, e.emp_no";
        return jdbc.queryForList(sql);
    }

    // Query 2 - department analysis by location (approximation using subqueries)
    @GetMapping("/q2")
    public List<Map<String, Object>> query2() {
        String sql = ""
                + "SELECT l.name AS location_name, "
                + "  (SELECT d.name FROM department d "
                + "     JOIN employee e2 ON d.code = e2.dept_code "
                + "     WHERE e2.location_id = l.id "
                + "     GROUP BY d.name "
                + "     ORDER BY COUNT(*) DESC LIMIT 1) AS dept_with_most_employees, "
                + "  (SELECT COUNT(*) FROM employee e3 "
                + "     WHERE e3.location_id = l.id AND e3.dept_code = (SELECT d2.code FROM department d2 "
                + "         JOIN employee e4 ON d2.code = e4.dept_code WHERE e4.location_id = l.id "
                + "         GROUP BY d2.code ORDER BY COUNT(*) DESC LIMIT 1)) AS dept_employee_count, "
                + "  (SELECT ROUND(AVG(e5.salary)::numeric,2) FROM employee e5 WHERE e5.location_id = l.id) AS avg_salary_of_lowest_dept "
                + "FROM location l";
        return jdbc.queryForList(sql);
    }

    // Query 3 - salary ranking + gap using correlated subqueries (no window
    // functions)
    @GetMapping("/q3")
    public List<Map<String, Object>> query3() {
        String sql = ""
                + "SELECT loc.name AS location_name, dep.name AS department_name, e.name AS employee_name, "
                + "  e.position AS position_name, e.salary AS salary, "
                + "  (1 + (SELECT COUNT(DISTINCT s.salary) FROM employee s "
                + "        WHERE s.location_id = e.location_id AND s.dept_code = e.dept_code AND s.salary > e.salary)) AS salary_rank, "
                + "  COALESCE((SELECT MAX(s2.salary) FROM employee s2 "
                + "        WHERE s2.location_id = e.location_id AND s2.dept_code = e.dept_code AND s2.salary > e.salary) - e.salary, 0) AS salary_gap "
                + "FROM employee e "
                + "LEFT JOIN department dep ON dep.code = e.dept_code "
                + "LEFT JOIN location loc ON loc.id = e.location_id "
                + "ORDER BY loc.name, dep.name, e.salary DESC";
        return jdbc.queryForList(sql);
    }
}