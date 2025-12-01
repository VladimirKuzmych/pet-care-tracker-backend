CREATE TABLE pet (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    kind VARCHAR(50) NOT NULL,
    breed VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_pet (
    user_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, pet_id),
    CONSTRAINT fk_user_pet_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_pet_pet FOREIGN KEY (pet_id) REFERENCES pet(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_pet_user_id ON user_pet(user_id);
CREATE INDEX idx_user_pet_pet_id ON user_pet(pet_id);
