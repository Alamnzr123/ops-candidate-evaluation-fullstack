package com.ops_candidate_evaluation.backend.importer;

import com.ops_candidate_evaluation.backend.model.*;
import com.ops_candidate_evaluation.backend.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;

@Component
public class DataImportRunner implements CommandLineRunner {

    private final LocationRepository locationRepo;
    private final DepartmentRepository departmentRepo;
    private final TierRepository tierRepo;
    private final EmployeeRepository employeeRepo;

    @Value("${app.import.path:sample-project}")
    private String importPath;

    public DataImportRunner(LocationRepository locationRepo,
            DepartmentRepository departmentRepo,
            TierRepository tierRepo,
            EmployeeRepository employeeRepo) {
        this.locationRepo = locationRepo;
        this.departmentRepo = departmentRepo;
        this.tierRepo = tierRepo;
        this.employeeRepo = employeeRepo;
    }

    @Override
    public void run(String... args) {
        try {
            Path base = Paths.get(importPath);
            if (!Files.exists(base)) {
                System.out.println("Import path not found: " + base.toAbsolutePath());
                return;
            }

            int locCount = importLocation(base.resolve("location.xlsx"));
            int deptCount = importDepartment(base.resolve("department.xlsx"));
            int tierCount = importTier(base.resolve("tier.xlsx"));
            int empCount = importEmployee(base.resolve("employee.xlsx"));

            System.out.printf("Import finished: locations=%d, departments=%d, tiers=%d, employees=%d%n",
                    locCount, deptCount, tierCount, empCount);

        } catch (Exception e) {
            System.err.println("Data import failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int importLocation(Path file) {
        if (!Files.exists(file))
            return 0;
        List<Location> list = new ArrayList<>();
        try (InputStream is = Files.newInputStream(file);
                Workbook wb = new XSSFWorkbook(is)) {
            Sheet s = wb.getSheetAt(0);
            Iterator<Row> rows = s.iterator();
            if (rows.hasNext())
                rows.next(); // skip header
            while (rows.hasNext()) {
                Row r = rows.next();
                String code = cellString(r, 0);
                String name = cellString(r, 1);
                if (code == null && name == null)
                    continue;
                Location l = new Location();
                l.setCode(nullable(code));
                l.setName(nullable(name));
                list.add(l);
            }
            locationRepo.saveAll(list);
        } catch (Exception ex) {
            System.err.println("importLocation error: " + ex.getMessage());
        }
        return list.size();
    }

    private int importDepartment(Path file) {
        if (!Files.exists(file))
            return 0;
        List<Department> list = new ArrayList<>();
        try (InputStream is = Files.newInputStream(file);
                Workbook wb = new XSSFWorkbook(is)) {
            Sheet s = wb.getSheetAt(0);
            Iterator<Row> rows = s.iterator();
            if (rows.hasNext())
                rows.next();
            while (rows.hasNext()) {
                Row r = rows.next();
                String code = cellString(r, 0);
                String name = cellString(r, 1);
                if (code == null && name == null)
                    continue;
                Department d = new Department();
                d.setCode(nullable(code));
                d.setName(nullable(name));
                list.add(d);
            }
            departmentRepo.saveAll(list);
        } catch (Exception ex) {
            System.err.println("importDepartment error: " + ex.getMessage());
        }
        return list.size();
    }

    private int importTier(Path file) {
        if (!Files.exists(file))
            return 0;
        List<Tier> list = new ArrayList<>();
        try (InputStream is = Files.newInputStream(file);
                Workbook wb = new XSSFWorkbook(is)) {
            Sheet s = wb.getSheetAt(0);
            Iterator<Row> rows = s.iterator();
            if (rows.hasNext())
                rows.next();
            while (rows.hasNext()) {
                Row r = rows.next();
                String code = cellString(r, 0);
                String name = cellString(r, 1);
                if (code == null && name == null)
                    continue;
                Tier t = new Tier();
                t.setCode(nullable(code));
                t.setName(nullable(name));
                list.add(t);
            }
            tierRepo.saveAll(list);
        } catch (Exception ex) {
            System.err.println("importTier error: " + ex.getMessage());
        }
        return list.size();
    }

    private int importEmployee(Path file) {
        if (!Files.exists(file))
            return 0;
        List<Employee> list = new ArrayList<>();
        Map<String, Location> locByCode = new HashMap<>();
        locationRepo.findAll().forEach(l -> {
            if (l.getCode() != null)
                locByCode.put(l.getCode(), l);
        });

        try (InputStream is = Files.newInputStream(file);
                Workbook wb = new XSSFWorkbook(is)) {
            Sheet s = wb.getSheetAt(0);
            Iterator<Row> rows = s.iterator();
            if (rows.hasNext())
                rows.next();
            while (rows.hasNext()) {
                Row r = rows.next();
                String empNo = cellString(r, 0); // emp_no
                String name = cellString(r, 1); // name
                String deptCode = cellString(r, 2); // dept_code
                String locationCodeOrId = cellString(r, 3); // location code or id
                String position = cellString(r, 4); // position
                BigDecimal salary = cellNumeric(r, 5); // salary

                if (empNo == null)
                    continue;
                Employee e = new Employee();
                e.setEmpNo(empNo);
                e.setName(nullable(name));
                e.setDeptCode(nullable(deptCode));
                e.setPosition(nullable(position));
                e.setSalary(salary);

                Long locationId = null;
                if (locationCodeOrId != null && !locationCodeOrId.isBlank()) {
                    // try parse as id
                    try {
                        locationId = Long.parseLong(locationCodeOrId);
                    } catch (Exception ex) {
                        // treat as code
                        Location loc = locByCode.get(locationCodeOrId);
                        if (loc != null)
                            locationId = loc.getId();
                    }
                }
                e.setLocationId(locationId);
                list.add(e);
            }
            employeeRepo.saveAll(list);
        } catch (Exception ex) {
            System.err.println("importEmployee error: " + ex.getMessage());
        }
        return list.size();
    }

    /* helpers */
    private static String cellString(Row r, int idx) {
        try {
            Cell c = r.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (c == null)
                return null;
            if (c.getCellType() == CellType.STRING)
                return c.getStringCellValue().trim();
            if (c.getCellType() == CellType.NUMERIC) {
                double d = c.getNumericCellValue();
                if (Math.floor(d) == d)
                    return String.valueOf((long) d);
                return BigDecimal.valueOf(d).toPlainString();
            }
            if (c.getCellType() == CellType.BOOLEAN)
                return Boolean.toString(c.getBooleanCellValue());
            return c.toString().trim();
        } catch (Exception ex) {
            return null;
        }
    }

    private static BigDecimal cellNumeric(Row r, int idx) {
        try {
            Cell c = r.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (c == null)
                return null;
            if (c.getCellType() == CellType.NUMERIC)
                return BigDecimal.valueOf(c.getNumericCellValue());
            String s = c.toString().trim();
            if (s.isEmpty())
                return null;
            return new BigDecimal(s.replaceAll(",", ""));
        } catch (Exception ex) {
            return null;
        }
    }

    private static String nullable(String s) {
        if (s == null)
            return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}