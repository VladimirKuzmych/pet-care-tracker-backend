CREATE TABLE feeding (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    grams DECIMAL(10, 2) NOT NULL,
    fed_at TIMESTAMP NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_feeding_pet FOREIGN KEY (pet_id) REFERENCES pet(id) ON DELETE CASCADE
);

CREATE INDEX idx_feeding_pet_id ON feeding(pet_id);
CREATE INDEX idx_feeding_fed_at ON feeding(fed_at);
CREATE INDEX idx_feeding_pet_id_fed_at ON feeding(pet_id, fed_at);
