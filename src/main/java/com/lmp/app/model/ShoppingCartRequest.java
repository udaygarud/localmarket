package com.lmp.app.model;

public class ShoppingCartRequest {

  private String userId;
  private String itemId;
  private int quantity;
  private boolean clearFirst;

  public ShoppingCartRequest() {
  }

  public ShoppingCartRequest(String userId) {
    this.userId = userId;
  }

  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public boolean isClearFirst() {
    return clearFirst;
  }

  public void setClearFirst(boolean clearFirst) {
    this.clearFirst = clearFirst;
  }
}
