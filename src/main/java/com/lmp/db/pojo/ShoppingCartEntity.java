package com.lmp.db.pojo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.lmp.app.entity.ShoppingCart;
import com.lmp.app.entity.ShoppingCart.CartItem;

@Document(collection="cart")
public class ShoppingCartEntity {

  @Id
  private String userId;
  private List<CartItem> items = new ArrayList<>();
  private double totalPrice;
  private String storeId;
  private boolean pickupOrder;
  private boolean deliveryOrder;
  private long created;
  private long updated;

  public static ShoppingCartEntity toEntity(ShoppingCart cart) {
    ShoppingCartEntity entity = new ShoppingCartEntity();
    BeanUtils.copyProperties(cart, entity);
    return entity;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public List<CartItem> getItems() {
    return items;
  }
  public void setItems(List<CartItem> items) {
    this.items = items;
  }
  public double getTotalPrice() {
    return totalPrice;
  }
  public void setTotalPrice(double totalPrice) {
    this.totalPrice = totalPrice;
  }
  public String getStoreId() {
    return storeId;
  }
  public void setStoreId(String storeId) {
    this.storeId = storeId;
  }
  public boolean isPickupOrder() {
    return pickupOrder;
  }
  public void setPickupOrder(boolean pickupOrder) {
    this.pickupOrder = pickupOrder;
  }
  public boolean isDeliveryOrder() {
    return deliveryOrder;
  }
  public void setDeliveryOrder(boolean deliveryOrder) {
    this.deliveryOrder = deliveryOrder;
  }
  public long getCreated() {
    return created;
  }
  public void setCreated(long created) {
    this.created = created;
  }
  public long getUpdated() {
    return updated;
  }
  public void setUpdated(long updated) {
    this.updated = updated;
  }

}
