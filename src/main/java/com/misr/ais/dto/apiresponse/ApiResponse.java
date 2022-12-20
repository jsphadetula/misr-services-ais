package com.misr.ais.dto.apiresponse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record ApiResponse<T>(T data, PageMeta meta, String message, List<String> errors) {

  public static record PageMeta(int size, int page, long total) {
  }

}
