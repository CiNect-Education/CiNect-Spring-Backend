-- Add OAuth provider fields to users table
ALTER TABLE users ALTER COLUMN password_hash DROP NOT NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS provider VARCHAR(20) DEFAULT 'LOCAL';
ALTER TABLE users ADD COLUMN IF NOT EXISTS provider_id VARCHAR(255);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_provider ON users(provider, provider_id)
  WHERE provider IS NOT NULL AND provider_id IS NOT NULL;
