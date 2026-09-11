-- Users choose their handle after registration; existing users remain unset.
ALTER TABLE service_user
    ADD COLUMN handle VARCHAR(30);

ALTER TABLE service_user
    ADD CONSTRAINT uk_service_user_handle UNIQUE (handle);

ALTER TABLE service_user
    ADD CONSTRAINT ck_service_user_handle_format
        CHECK (handle IS NULL OR handle ~ '^[a-z][a-z0-9_]{2,29}$');
