package com.misr.ais.dto.land;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record LandRequest(@NotBlank String name, //
    @Valid @NotEmpty List<Point> boundaries) {

  public static record Point(@NotNull Float latitude, @NotNull Float longitude) {
  }

}
