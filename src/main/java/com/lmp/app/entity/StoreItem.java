package com.lmp.app.entity;

import com.lmp.db.pojo.StoreEntity;

public class StoreItem {

  private StoreEntity store;
  private float listPrice;
  private boolean onSale;
  private float salePrice;
  private int popularity = 1;
  private int stock;
  private long added;
  private long updated;
  
  public StoreEntity getStore() {
    return store;
  }
  public void setStore(StoreEntity store) {
    this.store = store;
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
  
  
}
