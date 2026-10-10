CREATE TABLE app_user (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(100) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  enabled       BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uk_app_user_username UNIQUE (username)
);

CREATE TABLE role (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  CONSTRAINT uk_role_name UNIQUE (name)
);

CREATE TABLE user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES app_user (id),
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role (id)
);

INSERT INTO role (name) VALUES ('VIEWER'), ('EDITOR'), ('ADMIN');

INSERT INTO app_user (username, password_hash) VALUES
  ('viewer@dev', '$2a$10$vilK/Y3Eu114egB4iYLoHOx0CMzYl0EdmbW5up4xHkhRq.ompAtDq'),
  ('editor@dev', '$2a$10$.zzrIoZci5S8kLCmY5Ps5e44kkDjX3VkQrVaOqdzUgnwmtK9icK7O');

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM app_user u, role r WHERE u.username = 'viewer@dev' AND r.name = 'VIEWER';

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM app_user u, role r WHERE u.username = 'editor@dev' AND r.name = 'EDITOR';
