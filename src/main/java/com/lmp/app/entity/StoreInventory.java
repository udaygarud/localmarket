package com.lmp.app.entity;

import java.util.List;

public class StoreInventory implements Inventory{

  private String storeId;
  private List<Item> items;

  public StoreInventory(String storeId, List<Item> items) {
    this.storeId = storeId;
    this.items = items;
  }

  public String getStoreId() {
    return storeId;
  }
  public void setStoreId(String storeId) {
    this.storeId = storeId;
  }
  public List<Item> getItems() {
    return items;
  }
  public void setItems(List<Item> items) {
    this.items = items;
  }
}
