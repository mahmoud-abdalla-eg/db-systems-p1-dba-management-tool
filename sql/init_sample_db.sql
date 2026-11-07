-- =============================================================
-- Database Systems: Project 1 Sample Schema & Seed Data
-- =============================================================

CREATE TABLE IF NOT EXISTS departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(100) NOT NULL,
    budget DECIMAL(12, 2) DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    hire_date DATE NOT NULL,
    salary DECIMAL(10, 2) NOT NULL,
    department_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    action_type VARCHAR(20) NOT NULL,
    target_table VARCHAR(50) NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details VARCHAR(255)
);

-- Seed initial departments
INSERT IGNORE INTO departments (department_id, dept_name, location, budget) VALUES
(1, 'Computer Science', 'Main Campus - Science Hall', 750000.00),
(2, 'Software Engineering', 'Tech Park - Building B', 820000.00),
(3, 'Information Systems', 'Innovation Center', 450000.00),
(4, 'Data Analytics', 'Research Complex', 600000.00);

-- Seed initial employees
INSERT IGNORE INTO employees (employee_id, first_name, last_name, email, hire_date, salary, department_id) VALUES
(101, 'Amina', 'Li', 'amina.li@jnu.edu.cn', '2021-09-01', 12500.00, 1),
(102, 'Omar', 'Chen', 'omar.chen@jnu.edu.cn', '2020-03-15', 13800.00, 1),
(103, 'Mei', 'Hassan', 'mei.hassan@jnu.edu.cn', '2019-11-20', 14200.00, 2),
(104, 'Yusuf', 'Zhang', 'yusuf.zhang@jnu.edu.cn', '2022-02-10', 11900.00, 3),
(105, 'Sara', 'Wang', 'sara.wang@jnu.edu.cn', '2023-01-08', 11500.00, 4);

-- Seed initial audit log
INSERT INTO audit_logs (action_type, target_table, details) VALUES
('INITIALIZE', 'ALL', 'Database schema created and initial seed data populated.');

-- Create analytical view
CREATE OR REPLACE VIEW v_department_summary AS
SELECT 
    d.dept_name,
    d.location,
    COUNT(e.employee_id) AS total_employees,
    IFNULL(AVG(e.salary), 0.00) AS avg_salary,
    IFNULL(SUM(e.salary), 0.00) AS total_payroll
FROM departments d
LEFT JOIN employees e ON d.department_id = e.department_id
GROUP BY d.department_id, d.dept_name, d.location;
