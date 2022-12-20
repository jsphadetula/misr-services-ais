CREATE TABLE IF NOT EXISTS ais.land_configurations (
    id BIGSERIAL,
    cron VARCHAR(100) NOT NULL,
    max_retry INT NOT NULL,
    retry_interval_seconds INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id)
)