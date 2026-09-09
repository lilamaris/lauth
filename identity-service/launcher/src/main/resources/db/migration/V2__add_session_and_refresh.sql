CREATE TABLE user_session (
    id                  UUID DEFAULT uuidv7() NOT NULL,
    user_id             UUID NOT NULL,
    device              VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    last_used_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at          TIMESTAMP WITH TIME ZONE,

    CONSTRAINT pk_user_session PRIMARY KEY (id)
);

CREATE TABLE refresh_token (
    id                  UUID DEFAULT uuidv7() NOT NULL,
    session_id          UUID NOT NULL,
    token_hash          VARCHAR(255) NOT NULL,
    issued_at           TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    consumed_at         TIMESTAMP WITH TIME ZONE,

    CONSTRAINT pk_refresh_token PRIMARY KEY (id),
    CONSTRAINT uk_refresh_token_token_hash UNIQUE (token_hash)
);

CREATE INDEX ix_user_session_user_id
    ON user_session (user_id);

ALTER TABLE user_session
    ADD CONSTRAINT fk_user_session_user_id
    FOREIGN KEY (user_id)
    REFERENCES service_user (id)
    ON DELETE CASCADE;

CREATE INDEX ix_refresh_token_session_id
    ON refresh_token (session_id);

ALTER TABLE refresh_token
    ADD CONSTRAINT fk_refresh_token_session_id
    FOREIGN KEY (session_id)
    REFERENCES user_session (id)
    ON DELETE CASCADE;
