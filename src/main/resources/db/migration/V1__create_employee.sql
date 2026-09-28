CREATE TABLE employee (
  emp_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  emp_name   VARCHAR(255) NOT NULL,
  emp_age    INT NOT NULL,
  emp_city   VARCHAR(255) NOT NULL,
  emp_salary DECIMAL(10, 2) NOT NULL
);
