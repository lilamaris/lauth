CREATE TABLE federated_account (
    id                  UUID DEFAULT uuidv7() NOT NULL,
    user_id             UUID NOT NULL,
    registration_id     VARCHAR(255) NOT NULL,
    provider_user_id    VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_federated_account PRIMARY KEY (id),
    CONSTRAINT uk_federated_account_registration_id_provider_user_id
        UNIQUE (registration_id, provider_user_id)
);

ALTER TABLE federated_account
    ADD CONSTRAINT fk_federated_account_user_id
    FOREIGN KEY (user_id)
    REFERENCES service_user (id)
    ON DELETE CASCADE;