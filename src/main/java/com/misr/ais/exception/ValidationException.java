package com.misr.ais.exception;

import java.util.List;

public class ValidationException extends RuntimeException {

  private List<String> validationErrors;

  public ValidationException(List<String> validationErrors) {
    super("Validation error");

    this.validationErrors = validationErrors;
  }

  public List<String> getValidationErrors() {
    return this.validationErrors;
  }

}
