package com.lmp.app.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lmp.app.entity.Customer;
import com.lmp.app.service.CustomerService;
import com.lmp.app.utils.ValidationErrorBuilder;
import com.lmp.db.pojo.UserEntity;

@RestController
public class CustomerController extends BaseController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private CustomerService service;

  @RequestMapping(value = "/customer/register-new", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> registerStore(@Valid @RequestBody Customer customer, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    UserEntity user = service.registerUser(customer);
    if(user == null) {
      logger.debug("user registration failed");
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<UserEntity>(user, HttpStatus.OK);
  }
}
