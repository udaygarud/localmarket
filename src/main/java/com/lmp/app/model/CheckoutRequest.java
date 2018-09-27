package com.lmp.app.model;

import org.springframework.stereotype.Component;

@Component
public class CheckoutRequest {

  private String userId;
  private String orderId;

  public CheckoutRequest() {
  }

  public CheckoutRequest(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }
  
}
