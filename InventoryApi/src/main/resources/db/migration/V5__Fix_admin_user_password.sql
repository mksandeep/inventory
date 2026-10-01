-- Ensure default authentication roles exist
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrator with full access'),
('MANAGER', 'Manager with limited access'),
('USER', 'Regular user with read-only access')
ON CONFLICT (name) DO NOTHING;

-- Fix the seeded admin credentials so login with admin@example.com / admin123 works.
-- BCrypt hash generated for password: admin123
INSERT INTO users (email, password, full_name, active, created_at, updated_at)
VALUES (
    'admin@example.com',
    '$2a$10$CcE.cMrrHQsX3ClCY7uY8upF3o1mCTIeq2Te55Q4CyVnNd1V5ed.m',
    'Administrator',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (email) DO UPDATE
SET password = EXCLUDED.password,
    full_name = EXCLUDED.full_name,
    active = true,
    updated_at = NOW();

-- Ensure the admin user retains elevated roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u
JOIN roles r ON r.name = 'ADMIN'
WHERE u.email = 'admin@example.com'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u
JOIN roles r ON r.name = 'MANAGER'
WHERE u.email = 'admin@example.com'
ON CONFLICT DO NOTHING;

