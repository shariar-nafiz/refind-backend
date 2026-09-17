-- Ensure session operates in UTC
SET timezone = 'UTC';

-- ---------------------------------------------------------------------
-- Table: users
-- Description: Stores registered user accounts and credentials for ReFind
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(50) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'ROLE_USER',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    avatar_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table Comment
COMMENT ON TABLE users IS 'Stores registered user accounts and authentication credentials for the ReFind platform';

-- Column Comments
COMMENT ON COLUMN users.id IS 'Unique primary key identifier (BIGINT auto-increment)';
COMMENT ON COLUMN users.email IS 'User unique email address used for login and notifications';
COMMENT ON COLUMN users.phone IS 'User unique phone number used for login and privacy-gated handover disclosure';
COMMENT ON COLUMN users.password_hash IS 'BCrypt encrypted password hash';
COMMENT ON COLUMN users.full_name IS 'Full display name of the user';
COMMENT ON COLUMN users.role IS 'Authorization role assigned to the user (e.g. ROLE_USER, ROLE_ADMIN)';
COMMENT ON COLUMN users.status IS 'Lifecycle status of the account (e.g. ACTIVE, BLOCKED, INACTIVE, PENDING)';
COMMENT ON COLUMN users.avatar_url IS 'Direct or storage URL pointing to user profile avatar';
COMMENT ON COLUMN users.created_at IS 'UTC timestamp recorded when the user account is created';
COMMENT ON COLUMN users.updated_at IS 'UTC timestamp recorded when the user account details are last updated';

-- Performance Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_status ON users(status);
