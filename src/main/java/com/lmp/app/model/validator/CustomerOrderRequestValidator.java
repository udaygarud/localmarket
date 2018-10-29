package com.lmp.app.model.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.base.Strings;
import com.lmp.app.entity.OrderStatus;
import com.lmp.app.model.CustomerOrderRequest;

@Component
public class CustomerOrderRequestValidator implements Validator {

  public boolean supports(Class clazz) {
    return CustomerOrderRequest.class.equals(clazz);
  }

  public void validate(Object obj, Errors e) {
    CustomerOrderRequest cRequest = (CustomerOrderRequest) obj;
    if(Strings.isNullOrEmpty(cRequest.getUserId())) {
      e.reject("userId.required", "userId is required");
    }
    if(!Strings.isNullOrEmpty(cRequest.getOrderStatus())) {
      try {
        OrderStatus.valueOf(cRequest.getOrderStatus());
      } catch ( Exception ex) {
        e.reject("orderStatus.invalid", "invalid order status");
      }
    }
  }
}
