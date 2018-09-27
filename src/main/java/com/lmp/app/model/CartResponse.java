package com.lmp.app.model;

import java.util.List;

import com.lmp.app.entity.CustomerOrder;
import com.lmp.app.entity.ResponseStatus;
import com.lmp.app.entity.ShoppingCart;

public class CartResponse extends BaseResponse {

  private ShoppingCart cart;
  private List<String> itemIds;
  private CustomerOrder order;

  public CartResponse() {
  }

  public static CartResponse orderReceived(CustomerOrder order) {
    CartResponse cResponse = BaseResponse.responseStatus(ResponseStatus.ORDER_RECIEVED);
    cResponse.setOrder(order);
    return cResponse;
  }
  public static CartResponse orderPlaced(CustomerOrder order) {
    CartResponse cResponse = BaseResponse.responseStatus(ResponseStatus.ORDER_PLACED);
    cResponse.setOrder(order);
    return cResponse;
  }
  public static CartResponse orderCancelled(CustomerOrder order) {
    CartResponse cResponse = BaseResponse.responseStatus(ResponseStatus.ORDER_CANCELLED);
    cResponse.setOrder(order);
    return cResponse;
  }
  public static CartResponse productOutOfStock(List<String> itemIds) {
    CartResponse cResponse = BaseResponse.responseStatus(ResponseStatus.ITEM_OUT_OF_STOCK);
    cResponse.setItemIds(itemIds);
    return cResponse;
  }

  public CustomerOrder getOrder() {
    return order;
  }

  public void setOrder(CustomerOrder order) {
    this.order = order;
  }

  public List<String> getItemIds() {
    return itemIds;
  }

  public void setItemIds(List<String> itemIds) {
    this.itemIds = itemIds;
  }

  public ShoppingCart getCart() {
    return cart;
  }

  public void setCart(ShoppingCart cart) {
    this.cart = cart;
  }
}
