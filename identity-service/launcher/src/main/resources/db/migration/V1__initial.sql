CREATE TABLE service_user (
    id                  UUID DEFAULT uuidv7() NOT NULL,
    display_name        VARCHAR(100) NOT NULL,
    created_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_service_user PRIMARY KEY (id)
);

CREATE TABLE credential (
    id                  UUID DEFAULT uuidv7() NOT NULL,
    user_id             UUID NOT NULL,
    email               VARCHAR(50) NOT NULL,
    password_hash       VARCHAR(255) NOT NULL,
    created_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_credential PRIMARY KEY (id),
    CONSTRAINT uk_credential_email
        UNIQUE (email)
);

CREATE TABLE scope (
    id                  UUID DEFAULT uuidv7() NOT NULL,
    resource            VARCHAR(20) NOT NULL,
    action              VARCHAR(20) NOT NULL,
    description         VARCHAR(255) NOT NULL,
    created_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_scope PRIMARY KEY (id),
    CONSTRAINT uk_scope_resource_action
        UNIQUE (resource, action)
);

CREATE TABLE user_grant (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id             UUID NOT NULL,
    scope_id            UUID NOT NULL,
    created_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_user_grant PRIMARY KEY (id),
    CONSTRAINT uk_user_grant_user_id_scope_id
        UNIQUE (user_id, scope_id)
);

ALTER TABLE credential
    ADD CONSTRAINT fk_credential_user_id
    FOREIGN KEY (user_id)
    REFERENCES service_user (id)
    ON DELETE CASCADE;

ALTER TABLE user_grant
    ADD CONSTRAINT fk_user_grant_user_id
    FOREIGN KEY (user_id)
    REFERENCES service_user (id)
    ON DELETE CASCADE;

ALTER TABLE user_grant
    ADD CONSTRAINT fk_user_grant_scope_id
    FOREIGN KEY (scope_id)
    REFERENCES scope (id)
    ON DELETE CASCADE;