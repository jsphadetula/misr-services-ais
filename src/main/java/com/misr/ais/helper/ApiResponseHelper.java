package com.misr.ais.helper;

import java.util.List;

import org.springframework.data.domain.Page;

import com.misr.ais.dto.apiresponse.ApiResponse;
import com.misr.ais.dto.apiresponse.ApiResponse.PageMeta;
import com.misr.ais.exception.BadRequestException;
import com.misr.ais.exception.ResourceNotFoundException;
import com.misr.ais.exception.ValidationException;

public class ApiResponseHelper {

  public static <T> ApiResponse<T> build(T data) {
    return new ApiResponse<>(data, null, "Successful", null);
  }

  public static <T> ApiResponse<List<T>> build(Page<T> page) {
    var meta = new PageMeta(page.getSize(), page.getNumber(), page.getTotalElements());
    return new ApiResponse<>(page.getContent(), meta, "Successful", null);
  }

  public static <T> ApiResponse<T> build(ValidationException e) {
    return new ApiResponse<T>(null, null, "Validation Error", e.getValidationErrors());
  }

  public static <T> ApiResponse<T> build(BadRequestException e) {
    return new ApiResponse<T>(null, null, e.getMessage(), null);
  }

  public static <T> ApiResponse<T> build(ResourceNotFoundException e) {
    return new ApiResponse<T>(null, null, e.getMessage(), null);
  }

  public static <T> ApiResponse<T> build(Exception e) {
    return new ApiResponse<T>(null, null, "Internal Server Error", null);
  }

}
