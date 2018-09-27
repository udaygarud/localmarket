package com.lmp.app.entity;

import java.util.List;

import com.lmp.app.entity.ShoppingCart.CartItem;
import com.lmp.db.pojo.StoreEntity;
import com.lmp.db.pojo.UserEntity;

public class CustomerOrder {

  private String id;
  private String customerId;
  private String storeId;
  private List<CartItem> items;
  private OrderStatus status;
  private long orderedOn;
  private long lastUpdatedOn;
  private long completedOn;
  private double totalPrice;
  private boolean pickupOrder;

  public boolean isOrderCompleted() {
    return completedOn > 0 && completedOn < System.currentTimeMillis();
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public List<CartItem> getItems() {
    return items;
  }
  public String getCustomerId() {
    return customerId;
  }
  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }
  public String getStoreId() {
    return storeId;
  }
  public void setStoreId(String storeId) {
    this.storeId = storeId;
  }
  public void setItems(List<CartItem> items) {
    this.items = items;
  }
  public OrderStatus getStatus() {
    return status;
  }
  public void setStatus(OrderStatus status) {
    this.status = status;
  }
  public long getOrderedOn() {
    return orderedOn;
  }
  public void setOrderedOn(long orderedOn) {
    this.orderedOn = orderedOn;
  }
  public long getLastUpdatedOn() {
    return lastUpdatedOn;
  }
  public void setLastUpdatedOn(long lastUpdatedOn) {
    this.lastUpdatedOn = lastUpdatedOn;
  }
  public long getCompletedOn() {
    return completedOn;
  }
  public void setCompletedOn(long completedOn) {
    this.completedOn = completedOn;
  }
  public double getTotalPrice() {
    return totalPrice;
  }
  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }
  public boolean isPickupOrder() {
    return pickupOrder;
  }
  public void setPickupOrder(boolean pickupOrder) {
    this.pickupOrder = pickupOrder;
  }
}
