package com.lmp.app.entity;

public enum ResponseStatus {

  ORDER_RECIEVED(2001),
  ORDER_PLACED(2002),
  ORDER_CANCELLED(2003),
  MOVED_TO_LIST(2004),
  ADDED_TO_CART(2005),
  REMOVED_FROM_CART(2006),
  UPDATED_CART(2007),
  ORDER_UPDATED(2008),
  Unauthorized(3004),
  CART_NOT_FOUND(4001),
  ITEM_OUT_OF_STOCK(4002),
  ORDER_NOT_FOUND(4003),
  INVALID_ORDER_STATUS(4004),
  ITEM_NOT_FOUND(4005),
  DIFFERENT_STORE_ITEMS_IN_CART(4006),
  EMPTY_CART(4007),
  USER_ID_ALREADY_EXIST(4008);

  private int code;

  private ResponseStatus(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }
}
