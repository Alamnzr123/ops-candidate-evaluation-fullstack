package com.ops_candidate_evaluation.backend.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/query")
public class QueryController {

    private final JdbcTemplate jdbc;

    public QueryController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Query 1 - cumulative salary (use LATERAL correlated subquery to leverage
    // index)
    @GetMapping("/q1")
    public List<Map<String, Object>> query1() {
        String sql = ""
                + "SELECT e.dept_code AS dept_code, e.emp_no AS emp_no, e.name AS emp_name, COALESCE(cum.cumulative_salary, 0) AS cumulative_salary "
                + "FROM employee e "
                + "LEFT JOIN LATERAL ( "
                + "  SELECT SUM(b.salary) AS cumulative_salary "
                + "  FROM employee b "
                + "  WHERE (b.dept_code IS NOT DISTINCT FROM e.dept_code) "
                + "    AND b.emp_no <= e.emp_no "
                + ") cum ON true "
                + "ORDER BY e.dept_code, e.emp_no";
        return jdbc.queryForList(sql);
    }

    // Query 2 - department analysis by location (force UNASSIGNED fallback)
    @GetMapping("/q2")
    public List<Map<String, Object>> query2() {
        String sql = ""
                + "WITH dept_stats AS ( "
                + "  SELECT e.location_id, l.name AS location_name, d.code AS dept_code, "
                + "         COALESCE(NULLIF(d.name, ''), 'UNASSIGNED') AS dept_name, "
                + "         COUNT(*) AS emp_count, ROUND(AVG(e.salary)::numeric,2) AS avg_salary "
                + "  FROM employee e "
                + "  LEFT JOIN department d ON d.code::text = e.dept_code::text "
                + "  LEFT JOIN location l ON l.id = e.location_id "
                + "  GROUP BY e.location_id, l.name, d.code, d.name "
                + "), "
                + "most AS ( "
                + "  SELECT DISTINCT ON (location_id) location_id, dept_name AS dept_with_most_employees, emp_count AS dept_employee_count "
                + "  FROM dept_stats "
                + "  ORDER BY location_id, emp_count DESC, dept_name "
                + "), "
                + "lowest AS ( "
                + "  SELECT DISTINCT ON (location_id) location_id, avg_salary AS avg_salary_of_lowest_dept "
                + "  FROM dept_stats "
                + "  ORDER BY location_id, avg_salary ASC "
                + ") "
                + "SELECT COALESCE(l.name, 'UNASSIGNED') AS location_name, "
                + "       COALESCE(m.dept_with_most_employees, 'UNASSIGNED') AS dept_with_most_employees, "
                + "       m.dept_employee_count, lo.avg_salary_of_lowest_dept "
                + "FROM location l "
                + "LEFT JOIN most m ON m.location_id = l.id "
                + "LEFT JOIN lowest lo ON lo.location_id = l.id "
                + "ORDER BY location_name";
        return jdbc.queryForList(sql);
    }

    // Query 3 - salary ranking + gap (department_name fallback to UNASSIGNED)
    @GetMapping("/q3")
    public List<Map<String, Object>> query3() {
        String sql = ""
                + "WITH distinct_salaries AS ( "
                + "  SELECT location_id, dept_code, salary FROM employee GROUP BY location_id, dept_code, salary "
                + "), "
                + "salary_rank AS ( "
                + "  SELECT ds.location_id, ds.dept_code, ds.salary, 1 + COUNT(h.salary) AS salary_rank "
                + "  FROM distinct_salaries ds "
                + "  LEFT JOIN distinct_salaries h "
                + "    ON h.location_id = ds.location_id AND h.dept_code = ds.dept_code AND h.salary > ds.salary "
                + "  GROUP BY ds.location_id, ds.dept_code, ds.salary "
                + "), "
                + "next_salary AS ( "
                + "  SELECT ds.location_id, ds.dept_code, ds.salary, MIN(h.salary) AS next_higher_salary "
                + "  FROM distinct_salaries ds "
                + "  LEFT JOIN distinct_salaries h "
                + "    ON h.location_id = ds.location_id AND h.dept_code = ds.dept_code AND h.salary > ds.salary "
                + "  GROUP BY ds.location_id, ds.dept_code, ds.salary "
                + ") "
                + "SELECT COALESCE(loc.name, 'UNASSIGNED') AS location_name, "
                + "       COALESCE(NULLIF(dep.name, ''), 'UNASSIGNED') AS department_name, "
                + "       e.name AS employee_name, e.position AS position_name, e.salary AS salary, "
                + "       COALESCE(sr.salary_rank, 1) AS salary_rank, "
                + "       COALESCE(ns.next_higher_salary - e.salary, 0) AS salary_gap "
                + "FROM employee e "
                + "LEFT JOIN department dep ON dep.code::text = e.dept_code::text "
                + "LEFT JOIN location loc ON loc.id = e.location_id "
                + "LEFT JOIN salary_rank sr ON (sr.location_id IS NOT DISTINCT FROM e.location_id) AND (sr.dept_code IS NOT DISTINCT FROM e.dept_code) AND sr.salary = e.salary "
                + "LEFT JOIN next_salary ns ON (ns.location_id IS NOT DISTINCT FROM e.location_id) AND (ns.dept_code IS NOT DISTINCT FROM e.dept_code) AND ns.salary = e.salary "
                + "ORDER BY location_name, department_name, e.salary DESC";
        return jdbc.queryForList(sql);
    }
}