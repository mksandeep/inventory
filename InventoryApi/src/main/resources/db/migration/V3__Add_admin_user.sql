-- Insert default roles (should already exist from migration)
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrator with full access'),
('MANAGER', 'Manager with limited access'),
('USER', 'Regular user with read-only access')
ON CONFLICT DO NOTHING;

-- Create admin user (password: admin123 hashed with BCrypt)
-- Use this hash: $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5YmMxSUFutyal
INSERT INTO users (email, password, full_name, active, created_at, updated_at)
VALUES ('admin@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5YmMxSUFutyal', 'Administrator', true, NOW(), NOW())
ON CONFLICT (email) DO NOTHING;

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, roles r
WHERE u.email = 'admin@example.com' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Assign MANAGER role to admin user as well
INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, roles r
WHERE u.email = 'admin@example.com' AND r.name = 'MANAGER'
ON CONFLICT DO NOTHING;

