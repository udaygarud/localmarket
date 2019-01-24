package com.lmp.db.pojo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import com.lmp.app.entity.ShoppingWishList;
import com.lmp.app.entity.ShoppingWishList.WishItem;

@Document(collection="wishList")
public class ShoppingWishListEntity {
	@Id
	  private String userId;
	  private List<WishItem> items = new ArrayList<>();
	 // @DBRef
	 // private List<ItemEntity> item = new ArrayList<>();
	  private double totalPrice;
	 // private String storeId;
	  private boolean pickupOrder;
	  private boolean deliveryOrder;
	  private long created;
	  private long updated;

	  public static ShoppingWishListEntity toEntity(ShoppingWishList cart) {
		ShoppingWishListEntity entity = new ShoppingWishListEntity();
	    BeanUtils.copyProperties(cart, entity);
	    return entity;
	  }
	  
	 

	public String getUserId() {
	    return userId;
	  }
	  public void setUserId(String userId) {
	    this.userId = userId;
	  }
	  public List<WishItem> getItems() {
	    return items;
	  }
	  public void setItems(List<WishItem> items) {
	    this.items = items;
	  }
	  public double getTotalPrice() {
	    return totalPrice;
	  }
	  public void setTotalPrice(double totalPrice) {
	    this.totalPrice = totalPrice;
	  }
//	  public String getStoreId() {
//	    return storeId;
//	  }
//	  public void setStoreId(String storeId) {
//	    this.storeId = storeId;
//	  }
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

