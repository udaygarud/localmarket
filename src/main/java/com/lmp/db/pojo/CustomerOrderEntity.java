package com.lmp.db.pojo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.lmp.app.entity.CustomerOrder;
import com.lmp.app.entity.OrderStatus;
import com.lmp.app.entity.ShoppingCart;
import com.lmp.app.entity.ShoppingCart.CartItem;

@Document(collection="orders")
public class CustomerOrderEntity {

  @Id
  private String id;
  @Indexed
  private UserEntity customer;
  private String storeId;
  private List<CartItem> items;
  private OrderStatus status;
  private long orderedOn;
  private long lastUpdatedOn;
  private long completedOn;
  private double totalPrice;
  private boolean pickupOrder;

  public CustomerOrder toCustomerOrder() {
    CustomerOrder order = new CustomerOrder();
    BeanUtils.copyProperties(this, order);
    order.setCustomerId(this.customer.getId());
    return order;
  }

  public static List<CustomerOrder> toCustomerOrderList(List<CustomerOrderEntity> entities) {
    List<CustomerOrder> orders = new ArrayList<>();
    if(entities == null || entities.isEmpty()) {
      return orders;
    }
    for (CustomerOrderEntity entity : entities) {
      orders.add(entity.toCustomerOrder());
    }
    return orders;
  }

  public static CustomerOrderEntity fromCart(ShoppingCart cart) {
    CustomerOrderEntity entity = new CustomerOrderEntity().setStoreId(cart.getStoreId())
        .setCustomer(new UserEntity().setId(cart.getUserId()))
        .setItems(cart.getItems())
        .setOrderedOn(System.currentTimeMillis())
        .setPickupOrder(cart.isPickupOrder())
        .setTotalPrice(cart.getTotalPrice())
        .setStatus(OrderStatus.REVIEW);
    return entity;
  }
  public String getId() {
    return id;
  }
  public CustomerOrderEntity setId(String id) {
    this.id = id;
    return this;
  }
  public UserEntity getCustomer() {
    return customer;
  }
  public CustomerOrderEntity setCustomer(UserEntity customer) {
    this.customer = customer;
    return this;
  }
  public String getStoreId() {
    return storeId;
  }

  public CustomerOrderEntity setStoreId(String storeId) {
    this.storeId = storeId;
    return this;
  }

  public List<CartItem> getItems() {
    return items;
  }
  public CustomerOrderEntity setItems(List<CartItem> items) {
    this.items = items;
    return this;
  }
  public OrderStatus getStatus() {
    return status;
  }
  public CustomerOrderEntity setStatus(OrderStatus status) {
    this.status = status;
    return this;
  }
  public long getOrderedOn() {
    return orderedOn;
  }
  public CustomerOrderEntity setOrderedOn(long orderedOn) {
    this.orderedOn = orderedOn;
    return this;
  }
  public long getLastUpdatedOn() {
    return lastUpdatedOn;
  }
  public CustomerOrderEntity setLastUpdatedOn(long lastUpdatedOn) {
    this.lastUpdatedOn = lastUpdatedOn;
    return this;
  }
  public long getCompletedOn() {
    return completedOn;
  }
  public CustomerOrderEntity setCompletedOn(long completedOn) {
    this.completedOn = completedOn;
    return this;
  }
  public double getTotalPrice() {
    return totalPrice;
  }
  public CustomerOrderEntity setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
    return this;
  }
  public boolean isPickupOrder() {
    return pickupOrder;
  }
  public CustomerOrderEntity setPickupOrder(boolean pickupOrder) {
    this.pickupOrder = pickupOrder;
    return this;
  }
}
