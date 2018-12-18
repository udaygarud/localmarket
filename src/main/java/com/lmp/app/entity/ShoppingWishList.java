package com.lmp.app.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

//import com.lmp.app.entity.ShoppingWishList.WishItem;
import com.lmp.db.pojo.ShoppingWishListEntity;

public class ShoppingWishList {
	private String userId;
	  private List<WishItem> items = new ArrayList<>();
	  private double totalPrice;
	  private String storeId;
	  private boolean pickupOrder;
	  private boolean deliveryOrder;
	  private long created;
	  private long updated;

	  public static ShoppingWishList forUser(String userId) {
		ShoppingWishList cart = new ShoppingWishList();
	    cart.userId = userId;
	    return cart;
	  }
	  
	  public static ShoppingWishList fromEntity(ShoppingWishListEntity entity) {
	    ShoppingWishList cart = new ShoppingWishList();
	    BeanUtils.copyProperties(entity, cart);
	    return cart;
	  }

	  public static class WishItem {

	    private String id;
	    private String storeId;
	    private int stock;
	    private String title;
	    private String url;
	    private String brand;
	    private double listPrice;
	    private double offerPrice;
	    private boolean onSale;
	    private boolean inStock;
	    private List<Image> images;
	    private int quantity;
	    private boolean saveForLater;

	    public WishItem changeQuantities(int change) {
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
	    public int getStock() {
	      return stock;
	    }
	    public void setStock(int stock) {
	      this.stock = stock;
	    }
	    public String getTitle() {
	      return title;
	    }
	    public void setTitle(String title) {
	      this.title = title;
	    }
	    public String getUrl() {
	      return url;
	    }
	    public void setUrl(String url) {
	      this.url = url;
	    }
	    public String getBrand() {
	      return brand;
	    }
	    public void setBrand(String brand) {
	      this.brand = brand;
	    }
	    public double getListPrice() {
	      return listPrice;
	    }
	    public void setListPrice(double listPrice) {
	      this.listPrice = listPrice;
	    }
	    public double getOfferPrice() {
	      return offerPrice;
	    }
	    public void setOfferPrice(double offerPrice) {
	      this.offerPrice = offerPrice;
	    }
	    public boolean isOnSale() {
	      return onSale;
	    }
	    public void setOnSale(boolean onSale) {
	      this.onSale = onSale;
	    }
	    public boolean isInStock() {
	      return inStock;
	    }
	    public void setInStock(boolean inStock) {
	      this.inStock = inStock;
	    }
	    public List<Image> getImages() {
	      return images;
	    }
	    public void setImages(List<Image> images) {
	      this.images = images;
	    }
	    public boolean isSaveForLater() {
	      return saveForLater;
	    }
	    public void setSaveForLater(boolean saveForLater) {
	      this.saveForLater = saveForLater;
	    }
	    public int getQuantity() {
	      return quantity;
	    }
	    public WishItem setQuantity(int quantity) {
	      this.quantity = quantity;
	      return this;
	    }
	  }

	  public WishItem get(String itemId) {
	    for (WishItem cartItem : items) {
	      if(cartItem.getId().equals(itemId)) {
	        return cartItem;
	      }
	    }
	    return null;
	  }
	  public void addToWish(WishItem item, int quantity) {
	    WishItem existing = get(item.getId());
	    if(existing != null) {
	      if(existing.saveForLater) { // move from list to cart
	        existing.setQuantity(quantity);
	        existing.saveForLater = false;
	      } else {
	        existing.changeQuantities(quantity);
	      }
	    } else {
	      item.setQuantity(quantity);
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
	  public List<WishItem> getItems() {
	    return items;
	  }
	  public void setItems(List<WishItem> items) {
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
