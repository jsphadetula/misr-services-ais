CREATE TABLE IF NOT EXISTS ais.lands (
    id BIGSERIAL,
    name VARCHAR(100) NOT NULL,
    area DOUBLE PRECISION NOT NULL,
    land_configuration_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id),
    UNIQUE(name),
    FOREIGN KEY (land_configuration_id) REFERENCES land_configurations(id)
)