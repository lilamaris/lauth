-- clientId가 없는 password_reset_token은 새 도메인에서 어느 client를 위해 발급된 credential인지 증명할 수 없음
DELETE FROM password_reset_token;

ALTER TABLE password_reset_token
    ADD COLUMN client_id VARCHAR(100) NOT NULL;