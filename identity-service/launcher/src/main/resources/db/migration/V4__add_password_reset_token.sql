CREATE TABLE password_reset_token (
    id                  UUID DEFAULT uuidv7() NOT NULL,
    credential_id       UUID NOT NULL,
    token_hash          VARCHAR(255) NOT NULL,
    issued_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at          TIMESTAMP WITH TIME ZONE,
    consumed_at         TIMESTAMP WITH TIME ZONE,

    CONSTRAINT pk_password_reset_token PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_password_reset_token
    ON password_reset_token (credential_id)
    WHERE consumed_at IS NULL
        AND revoked_at IS NULL;

ALTER TABLE password_reset_token
    ADD CONSTRAINT fk_password_reset_token
    FOREIGN KEY (credential_id)
    REFERENCES credential (id)
    ON DELETE CASCADE;