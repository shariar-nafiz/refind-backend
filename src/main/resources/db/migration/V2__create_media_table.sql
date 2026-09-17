-- Ensure session operates in UTC
SET timezone = 'UTC';

-- ---------------------------------------------------------------------
-- Table: media
-- Description: Stores uploaded media files (avatars, item images, proof documents)
-- ---------------------------------------------------------------------
CREATE TABLE media (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table Comment
COMMENT ON TABLE media IS 'Stores metadata and storage pointers for uploaded files across ReFind (avatars, item photos, proof documents)';

-- Column Comments
COMMENT ON COLUMN media.id IS 'Unique primary key identifier (BIGINT auto-increment)';
COMMENT ON COLUMN media.user_id IS 'Foreign key referencing the user who uploaded this media file; nullified if user account is deleted';
COMMENT ON COLUMN media.file_name IS 'Original or sanitized human-readable filename';
COMMENT ON COLUMN media.file_type IS 'MIME media type of the file (e.g. image/jpeg, image/png, image/webp)';
COMMENT ON COLUMN media.file_size IS 'Total size of the file in bytes';
COMMENT ON COLUMN media.file_url IS 'Public relative or absolute access URL to retrieve or view the file';
COMMENT ON COLUMN media.storage_path IS 'Underlying local or cloud storage path where the file is physically stored';
COMMENT ON COLUMN media.created_at IS 'UTC timestamp recorded when the file was uploaded';
COMMENT ON COLUMN media.updated_at IS 'UTC timestamp recorded when media metadata was last updated';

-- Performance Indexes
CREATE INDEX idx_media_user_id ON media(user_id);
CREATE INDEX idx_media_created_at ON media(created_at);
