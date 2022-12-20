package com.misr.ais.dto.land;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ConfigureLandRequest(@NotBlank String cron, //
    @Positive @NotNull Integer maxRetry, //
    @Positive @NotNull Integer retryIntervalSeconds) {
}
