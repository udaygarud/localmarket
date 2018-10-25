package com.lmp.app.utils;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import com.lmp.app.model.validator.ValidationError;

public class ValidationErrorBuilder {

  public static ValidationError fromBindingErrors(Errors errors) {
      ValidationError error = new ValidationError("Validation failed. " + errors.getErrorCount() + " error(s)");
      for (ObjectError objectError : errors.getAllErrors()) {
          error.addValidationError(objectError.getDefaultMessage());
      }
      return error;
  }
}
