package com.lmp.app.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.Assert;

import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.pojo.ShoppingCartEntity;
import com.lmp.db.pojo.StoreItemEntity;

public class ShoppingCart {

  private String userId;
  private List<CartItem> items = new ArrayList<>();
  private double totalPrice;
  private String storeId;
  private boolean pickupOrder;
  private boolean deliveryOrder;
  private long created;
  private long updated;

  public static ShoppingCart forUser(String userId) {
    ShoppingCart cart = new ShoppingCart();
    cart.userId = userId;
    return cart;
  }
  
  public static ShoppingCart fromEntity(ShoppingCartEntity entity) {
    ShoppingCart cart = new ShoppingCart();
    BeanUtils.copyProperties(entity, cart);
    return cart;
  }

  public static class CartItem {

    private String id;
    @DBRef
    private StoreItemEntity storeItem = new StoreItemEntity();
  //  private List<Image> images;
    private String storeId;
    private int quantity;
    private boolean saveForLater;

    public CartItem changeQuantities(int change) {
      this.quantity += change;
      this.quantity = Math.max(0, this.quantity);
      return this;
    }
    public String getId() {
      return id;
    }
    public void setId(String id) {
      this.id = id;
    }
    public String getStoreId() {
      return storeId;
    }
    public void setStoreId(String storeId) {
      this.storeId = storeId;
    }
//    public int getStock() {
//      return stock;
//    }
//    public void setStock(int stock) {
//      this.stock = stock;
//    }

    public boolean isSaveForLater() {
      return saveForLater;
    }
    public void setSaveForLater(boolean saveForLater) {
      this.saveForLater = saveForLater;
    }
    public int getQuantity() {
      return quantity;
    }
    public CartItem setQuantity(int quantity) {
      this.quantity = quantity;
      return this;
    }
	public StoreItemEntity getItem() {
		return storeItem;
	}
	public void setItem(StoreItemEntity item) {
		this.storeItem = item;
	}
    
  }

  public CartItem get(String itemId) {
    for (CartItem cartItem : items) {
      if(cartItem.getId().equals(itemId)) {
        return cartItem;
      }
    }
    return null;
  }
  public void addToCart(CartItem item, int quantity,StoreItemEntity ie) {
    CartItem existing = get(item.getId());
    if(existing != null) {
      if(existing.saveForLater) { // move from list to cart
        existing.setQuantity(quantity);
        existing.saveForLater = false;
      } else {
        existing.changeQuantities(quantity);
      }
    } else {
    	
      item.setQuantity(quantity);
      item.setItem(ie);
      items.add(item);
    }
  }

  public void remove(String itemOrderId) {
    if(get(itemOrderId) == null) {
      return ;
    }
    items.remove(get(itemOrderId));
    if(items.isEmpty()) {
      storeId = null;
    }
  }

  public void update(String itemId, int quantity) {
    if(get(itemId) == null) {
      return ;
    }
    get(itemId).setQuantity(quantity);
  }

  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
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
  public List<CartItem> getItems() {
    return items;
  }
  public void setItems(List<CartItem> items) {
    this.items = items;
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
