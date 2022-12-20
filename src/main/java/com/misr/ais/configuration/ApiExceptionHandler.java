package com.misr.ais.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.misr.ais.dto.apiresponse.ApiResponse;
import com.misr.ais.exception.BadRequestException;
import com.misr.ais.exception.ResourceNotFoundException;
import com.misr.ais.exception.ServerException;
import com.misr.ais.exception.ValidationException;
import com.misr.ais.helper.ApiResponseHelper;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  @Nullable
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {
    List<String> errors = new ArrayList<>();

    ex.getBindingResult().getFieldErrors().forEach(x -> errors.add(x.getField() + ": " + x.getDefaultMessage()));

    ex.getBindingResult().getGlobalErrors().forEach(x -> errors.add(x.getObjectName() + ": " + x.getDefaultMessage()));

    var e = new ValidationException(errors);
    var response = ApiResponseHelper.build(e);
    return new ResponseEntity<>(response, null, status);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponse<Object>> handleApplicationExeption(BadRequestException e) {
    var response = ApiResponseHelper.build(e);
    return new ResponseEntity<>(response, null, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiResponse<Object>> handleApplicationExeption(ValidationException e) {
    var response = ApiResponseHelper.build(e);
    return new ResponseEntity<>(response, null, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleApplicationExeption(ResourceNotFoundException e) {
    var response = ApiResponseHelper.build(e);
    return new ResponseEntity<>(response, null, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ServerException.class)
  public ResponseEntity<ApiResponse<Object>> handleApplicationExeption(ServerException e, HttpServletRequest request) {
    var response = ApiResponseHelper.build(e);
    return new ResponseEntity<>(response, null, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse<Object>> handleApplicationExeption(RuntimeException e, HttpServletRequest request) {
    var response = ApiResponseHelper.build(e);
    return new ResponseEntity<>(response, null, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
