-- Ensure session operates in UTC
SET timezone = 'UTC';

-- ---------------------------------------------------------------------
-- 1. Enhance users table with bio, secondary phone, and onboarding flag
-- ---------------------------------------------------------------------
ALTER TABLE users ADD COLUMN bio VARCHAR(500);
ALTER TABLE users ADD COLUMN secondary_phone VARCHAR(50);
ALTER TABLE users ADD COLUMN is_profile_completed BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN users.bio IS 'User biographical summary or personal notes';
COMMENT ON COLUMN users.secondary_phone IS 'Alternative contact phone or WhatsApp number';
COMMENT ON COLUMN users.is_profile_completed IS 'Flag indicating whether user has completed initial profile setup and onboarding';

CREATE INDEX idx_users_is_profile_completed ON users(is_profile_completed);

-- ---------------------------------------------------------------------
-- 2. Table: user_addresses
-- Description: Stores global address locations (home, campus, work) for users
-- ---------------------------------------------------------------------
CREATE TABLE user_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    address_type VARCHAR(50) NOT NULL DEFAULT 'HOME',
    country VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    street_address VARCHAR(255) NOT NULL,
    address_line_2 VARCHAR(255),
    postal_code VARCHAR(20),
    is_default BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE user_addresses IS 'Stores internationalized physical and delivery address profiles for users';
COMMENT ON COLUMN user_addresses.id IS 'Unique primary key identifier (BIGINT auto-increment)';
COMMENT ON COLUMN user_addresses.user_id IS 'Foreign key referencing the user who owns this address; deleted on user cascade';
COMMENT ON COLUMN user_addresses.address_type IS 'Category or label of the address (e.g. HOME, WORK, CAMPUS, OTHER)';
COMMENT ON COLUMN user_addresses.country IS 'Country name or standard country identifier';
COMMENT ON COLUMN user_addresses.state IS 'State, province, division, or administrative region';
COMMENT ON COLUMN user_addresses.city IS 'City, town, or municipality';
COMMENT ON COLUMN user_addresses.street_address IS 'Primary street address, building number, and street name';
COMMENT ON COLUMN user_addresses.address_line_2 IS 'Secondary address details such as apartment, suite, unit, or floor';
COMMENT ON COLUMN user_addresses.postal_code IS 'Postal or ZIP routing code';
COMMENT ON COLUMN user_addresses.is_default IS 'Flag indicating if this is the user primary default address for item matches and reports';
COMMENT ON COLUMN user_addresses.created_at IS 'UTC timestamp recorded when the address record was created';
COMMENT ON COLUMN user_addresses.updated_at IS 'UTC timestamp recorded when the address record was last updated';

CREATE INDEX idx_user_addresses_user_id ON user_addresses(user_id);
CREATE INDEX idx_user_addresses_country_city ON user_addresses(country, city);

-- ---------------------------------------------------------------------
-- 3. Table: user_preferences
-- Description: Stores user notification, privacy, and app localization preferences
-- ---------------------------------------------------------------------
CREATE TABLE user_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    email_notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    push_notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    match_alerts_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    claim_alerts_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    preferred_contact_method VARCHAR(30) NOT NULL DEFAULT 'EMAIL',
    show_phone_on_claim_approved BOOLEAN NOT NULL DEFAULT TRUE,
    show_email_on_claim_approved BOOLEAN NOT NULL DEFAULT TRUE,
    language VARCHAR(10) NOT NULL DEFAULT 'en',
    theme VARCHAR(20) NOT NULL DEFAULT 'SYSTEM',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE user_preferences IS 'Stores notification subscriptions, privacy disclosure rules, and app UI preferences per user';
COMMENT ON COLUMN user_preferences.id IS 'Unique primary key identifier (BIGINT auto-increment)';
COMMENT ON COLUMN user_preferences.user_id IS 'Unique foreign key referencing the owner user; deleted on user cascade';
COMMENT ON COLUMN user_preferences.email_notifications_enabled IS 'Master flag for sending transactional and activity notification emails';
COMMENT ON COLUMN user_preferences.push_notifications_enabled IS 'Master flag for dispatching mobile push notifications';
COMMENT ON COLUMN user_preferences.match_alerts_enabled IS 'Flag for receiving instant alerts when smart matching finds relevant lost/found items';
COMMENT ON COLUMN user_preferences.claim_alerts_enabled IS 'Flag for receiving alerts when claims are submitted or status changes';
COMMENT ON COLUMN user_preferences.preferred_contact_method IS 'User preferred communication channel (e.g. EMAIL, PHONE, WHATSAPP, IN_APP)';
COMMENT ON COLUMN user_preferences.show_phone_on_claim_approved IS 'Controls whether user phone is disclosed to claimant once claim is approved';
COMMENT ON COLUMN user_preferences.show_email_on_claim_approved IS 'Controls whether user email is disclosed to claimant once claim is approved';
COMMENT ON COLUMN user_preferences.language IS 'User preferred application UI language code (e.g. en, bn)';
COMMENT ON COLUMN user_preferences.theme IS 'User preferred application visual theme (e.g. LIGHT, DARK, SYSTEM)';
COMMENT ON COLUMN user_preferences.created_at IS 'UTC timestamp recorded when the user preferences were initialized';
COMMENT ON COLUMN user_preferences.updated_at IS 'UTC timestamp recorded when the user preferences were last modified';

CREATE UNIQUE INDEX uq_user_preferences_user_id ON user_preferences(user_id);
