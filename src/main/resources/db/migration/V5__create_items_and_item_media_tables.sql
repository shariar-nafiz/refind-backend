-- Flyway Migration: V5__create_items_and_item_media_tables.sql
-- Description: Create items table and item_media join table with indexing

SET timezone = 'UTC';

-- ---------------------------------------------------------------------
-- 1. Table: items
-- Description: Core repository of lost and found item reports
-- ---------------------------------------------------------------------
CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE RESTRICT,
    type VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    specific_location_hint VARCHAR(255),
    incident_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    secret_identifier_question VARCHAR(255),
    secret_identifier_answer VARCHAR(255),
    tags TEXT[] NOT NULL DEFAULT '{}',
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    view_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_items_type CHECK (type IN ('LOST', 'FOUND')),
    CONSTRAINT chk_items_status CHECK (status IN ('OPEN', 'CLAIM_PENDING', 'RESOLVED', 'EXPIRED', 'ARCHIVED'))
);

COMMENT ON TABLE items IS 'Lost and found item reports registered by platform users';
COMMENT ON COLUMN items.id IS 'Unique item report identifier';
COMMENT ON COLUMN items.user_id IS 'Foreign key referencing user who created this post';
COMMENT ON COLUMN items.category_id IS 'Classification category of the item';
COMMENT ON COLUMN items.location_id IS 'Official geographic location (district & thana) where incident occurred';
COMMENT ON COLUMN items.type IS 'Report nature: LOST or FOUND';
COMMENT ON COLUMN items.title IS 'Short descriptive title of the item';
COMMENT ON COLUMN items.description IS 'Detailed visual and contextual description of the item';
COMMENT ON COLUMN items.specific_location_hint IS 'Granular landmark or local hint (e.g., Gate 2, TSC cafeteria)';
COMMENT ON COLUMN items.incident_date_time IS 'Estimated or known date and time when the item was lost or found';
COMMENT ON COLUMN items.secret_identifier_question IS 'Prompt question for ownership verification (FOUND items only)';
COMMENT ON COLUMN items.secret_identifier_answer IS 'Confidential answer for ownership verification (FOUND items only)';
COMMENT ON COLUMN items.tags IS 'Searchable keywords and descriptors for matching engine';
COMMENT ON COLUMN items.status IS 'Lifecycle state of report: OPEN, CLAIM_PENDING, RESOLVED, EXPIRED, ARCHIVED';
COMMENT ON COLUMN items.view_count IS 'Total views recorded for this item report';

CREATE INDEX idx_items_user_id ON items(user_id);
CREATE INDEX idx_items_category_id ON items(category_id);
CREATE INDEX idx_items_location_id ON items(location_id);
CREATE INDEX idx_items_type_status ON items(type, status);
CREATE INDEX idx_items_incident_date_time ON items(incident_date_time);
CREATE INDEX idx_items_created_at ON items(created_at);
CREATE INDEX idx_items_tags ON items USING GIN(tags);

-- ---------------------------------------------------------------------
-- 2. Table: item_media
-- Description: Association between items and uploaded media assets
-- ---------------------------------------------------------------------
CREATE TABLE item_media (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
    media_id BIGINT NOT NULL REFERENCES media(id) ON DELETE CASCADE,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_item_media_item_media UNIQUE (item_id, media_id)
);

COMMENT ON TABLE item_media IS 'Join table linking items to their attached photos and documents';
COMMENT ON COLUMN item_media.item_id IS 'Foreign key referencing the item report';
COMMENT ON COLUMN item_media.media_id IS 'Foreign key referencing the uploaded media asset';
COMMENT ON COLUMN item_media.is_primary IS 'Flag designating thumbnail/cover image';
COMMENT ON COLUMN item_media.display_order IS 'Visual sequencing order in photo gallery';

CREATE INDEX idx_item_media_item_id ON item_media(item_id);
CREATE INDEX idx_item_media_media_id ON item_media(media_id);
