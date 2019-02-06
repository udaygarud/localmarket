package com.lmp.db.pojo;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.lmp.app.entity.ShoppingCart.CartItem;
import com.lmp.app.entity.ShoppingWishList.WishItem;

@Document(collection="storeInventory")
@TypeAlias("storeInventory")
@CompoundIndexes( {
  @CompoundIndex(name = "store_items",
      unique = true,
      def = "{'storeId' : 1, 'item.$id' : 1, 'popularity' : -1 }")
})
public class StoreItemEntity implements Comparable<StoreItemEntity>{

  @Id
  private String id;
  private String storeId;
  @DBRef
  private ItemEntity item = new ItemEntity();
  private float listPrice;
  @Indexed
  private boolean onSale;
  private float salePrice;
  private int popularity = 1;
  private int stock;
  private long added;
  private long updated;

  public CartItem toCartItem() {
    CartItem ci = new CartItem();
    BeanUtils.copyProperties(this.getItem(), ci);
    BeanUtils.copyProperties(this, ci);
   // ci.setListPrice(Math.round(this.getListPrice() * 100.0) / 100.0);
  //  ci.setOfferPrice(Math.round(this.getSalePrice() * 100.0) / 100.0);
    ci.setStoreId(this.storeId);
    return ci;
  }
  
//  public WishItem toWishItem() {
//	    WishItem ci = new WishItem();
//	    BeanUtils.copyProperties(this.getItem(), ci);
//	    BeanUtils.copyProperties(this, ci);
//	    ci.setListPrice(Math.round(this.getListPrice() * 100.0) / 100.0);
//	    ci.setOfferPrice(Math.round(this.getSalePrice() * 100.0) / 100.0);
//	  //  ci.setStoreId(this.storeId);
//	    return ci;
//	  }

  
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
  public ItemEntity getItem() {
    return item;
  }
  public void setItem(ItemEntity item) {
    this.item = item;
  }
  public int getStock() {
    return stock;
  }
  public void setStock(int stock) {
    this.stock = stock;
  }
  public long getAdded() {
    return added;
  }
  public void setAdded(long added) {
    this.added = added;
  }
  public long getUpdated() {
    return updated;
  }
  public void setUpdated(long updated) {
    this.updated = updated;
  }
  public float getListPrice() {
    return listPrice;
  }
  public void setListPrice(float listPrice) {
    this.listPrice = listPrice;
  }
  public boolean isOnSale() {
    return onSale;
  }
  public void setOnSale(boolean onSale) {
    this.onSale = onSale;
  }
  public float getSalePrice() {
    return salePrice;
  }
  public void setSalePrice(float salePrice) {
    this.salePrice = salePrice;
  }
  public int getPopularity() {
    return popularity;
  }
  public void setPopularity(int popularity) {
    this.popularity = popularity;
  }

  @Override
  public int compareTo(StoreItemEntity o) {
    int res = this.storeId.compareTo(o.storeId) ;
    if(res == 0) {
      return Integer.compare(this.popularity, o.popularity);
    }
    return res;
  }
}
