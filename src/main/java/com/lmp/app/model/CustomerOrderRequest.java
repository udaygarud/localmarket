package com.lmp.app.model;

public class CustomerOrderRequest extends PageableRequest {

  private String orderId;
  private String userId;
  private String storeId;
  private String orderStatus;

  public CustomerOrderRequest() {
  }

  public CustomerOrderRequest(int page, int rows) {
    super(page, rows);
  }

  public boolean isGetAllStatusRequest() {
    if (orderStatus == null || orderStatus.trim().isEmpty()) {
      return true;
    }
    return false;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getStoreId() {
    return storeId;
  }

  public void setStoreId(String storeId) {
    this.storeId = storeId;
  }

  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }
}
