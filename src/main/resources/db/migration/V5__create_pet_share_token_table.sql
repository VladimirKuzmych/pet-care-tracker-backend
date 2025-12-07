CREATE TABLE pet_share_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    pet_id BIGINT NOT NULL,
    created_by_user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (pet_id) REFERENCES pet(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_user_id) REFERENCES user_account(id) ON DELETE CASCADE
);

CREATE INDEX idx_pet_share_token_token ON pet_share_token(token);
CREATE INDEX idx_pet_share_token_pet_id ON pet_share_token(pet_id);
CREATE INDEX idx_pet_share_token_created_by ON pet_share_token(created_by_user_id);
CREATE INDEX idx_pet_share_token_active ON pet_share_token(is_active, expires_at);
