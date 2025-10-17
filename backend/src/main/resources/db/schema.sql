-- Schema DDL for candidate evaluation project (PostgreSQL-specific, using BIGSERIAL)

-- Locations
CREATE TABLE IF NOT EXISTS location (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(64) UNIQUE,
  name VARCHAR(255)
);

-- Departments
CREATE TABLE IF NOT EXISTS department (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(255)
);

-- Tiers
CREATE TABLE IF NOT EXISTS tier (
  id BIGSERIAL PRIMARY KEY,
  code VARCHAR(64) UNIQUE,
  name VARCHAR(255)
);

-- Employees
CREATE TABLE IF NOT EXISTS employee (
  id BIGSERIAL PRIMARY KEY,
  emp_no VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(255),
  dept_code VARCHAR(64),
  location_id BIGINT,
  position VARCHAR(255),
  salary NUMERIC(15,2),
  CONSTRAINT fk_employee_location FOREIGN KEY (location_id) REFERENCES location(id) ON DELETE SET NULL,
  CONSTRAINT fk_employee_dept_code FOREIGN KEY (dept_code) REFERENCES department(code) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_employee_dept_code ON employee(dept_code);
CREATE INDEX IF NOT EXISTS idx_employee_location_id ON employee(location_id);

-- API call history
CREATE TABLE IF NOT EXISTS api_call_history (
  id BIGSERIAL PRIMARY KEY,
  timestamp TIMESTAMPTZ,
  method VARCHAR(16),
  path VARCHAR(1024),
  status INTEGER,
  user_identifier VARCHAR(255)
);