package com.lmp.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.lmp.app.model.validator.ValidationError;
import com.lmp.app.utils.ValidationErrorBuilder;

public class BaseController {

  @ExceptionHandler
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public ValidationError handleException(MethodArgumentNotValidException exception) {
      return createValidationError(exception);
  }

  private ValidationError createValidationError(MethodArgumentNotValidException exception) {
      return ValidationErrorBuilder.fromBindingErrors(exception.getBindingResult());
  }
}
