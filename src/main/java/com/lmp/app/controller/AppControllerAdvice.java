package com.lmp.app.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.lmp.app.entity.ResponseStatus;
import com.lmp.app.exceptions.EmptyCartException;
import com.lmp.app.exceptions.InvalidOrderStatusException;
import com.lmp.app.exceptions.ItemNotFoundException;
import com.lmp.app.exceptions.ItemNotInStockException;
import com.lmp.app.exceptions.MuliplteStoreInCartException;
import com.lmp.app.exceptions.OrderNotFoundException;
import com.lmp.app.exceptions.UnauthorizedException;
import com.lmp.app.exceptions.UserIdAlreadyExistException;
import com.lmp.app.model.BaseResponse;
import com.lmp.app.model.CartResponse;
import com.lmp.app.model.validator.ValidationError;
import com.lmp.app.utils.ValidationErrorBuilder;

@ControllerAdvice
public class AppControllerAdvice extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    ValidationError error = ValidationErrorBuilder.fromBindingErrors(exception.getBindingResult());
    return super.handleExceptionInternal(exception, error, headers, status, request);
  }

  @ExceptionHandler({ EmptyCartException.class })
  public ResponseEntity<BaseResponse> handleEmptyCartException(Exception ex, WebRequest request) {
    return new ResponseEntity<BaseResponse>(BaseResponse.responseStatus(ResponseStatus.EMPTY_CART), HttpStatus.OK);
  }

  @ExceptionHandler({ UnauthorizedException.class })
  public ResponseEntity<BaseResponse> handleUnauthorizedException(Exception ex, WebRequest request) {
    return new ResponseEntity<BaseResponse>(BaseResponse.responseStatus(ResponseStatus.Unauthorized), HttpStatus.OK);
  }

  @ExceptionHandler({ ItemNotInStockException.class })
  public ResponseEntity<CartResponse> handleProductNotInStockException(Exception ex, WebRequest request) {
    ItemNotInStockException e = (ItemNotInStockException) ex;
    return new ResponseEntity<CartResponse>(CartResponse.productOutOfStock(e.getOutOfStockItems()), HttpStatus.OK);
  }

  @ExceptionHandler({ OrderNotFoundException.class })
  public ResponseEntity<BaseResponse> handleOrderNotFoundException(Exception ex, WebRequest request) {
    OrderNotFoundException e = (OrderNotFoundException) ex;
    return new ResponseEntity<BaseResponse>(BaseResponse.responseStatus(ResponseStatus.ORDER_NOT_FOUND), HttpStatus.OK);
  }

  @ExceptionHandler({ UserIdAlreadyExistException.class })
  public ResponseEntity<BaseResponse> handleUserIdAlreadyExistException(Exception ex, WebRequest request) {
    UserIdAlreadyExistException e = (UserIdAlreadyExistException) ex;
    return new ResponseEntity<BaseResponse>(BaseResponse.responseStatus(ResponseStatus.USER_ID_ALREADY_EXIST), HttpStatus.OK);
  }

  @ExceptionHandler({ InvalidOrderStatusException.class })
  public ResponseEntity<BaseResponse> handleInvalidOrderStatusException(Exception ex, WebRequest request) {
    return new ResponseEntity<BaseResponse>(BaseResponse.responseStatus(ResponseStatus.INVALID_ORDER_STATUS),
        HttpStatus.OK);
  }

  @ExceptionHandler({ ItemNotFoundException.class })
  public ResponseEntity<BaseResponse> handleItemNotFoundException(Exception ex, WebRequest request) {
    return new ResponseEntity<BaseResponse>(BaseResponse.responseStatus(ResponseStatus.ITEM_NOT_FOUND),
        HttpStatus.OK);
  }

  @ExceptionHandler({ MuliplteStoreInCartException.class })
  public ResponseEntity<BaseResponse> handleMuliplteStoreInCartException(Exception ex, WebRequest request) {
    return new ResponseEntity<BaseResponse>(BaseResponse.responseStatus(ResponseStatus.DIFFERENT_STORE_ITEMS_IN_CART),
        HttpStatus.OK);
  }
}