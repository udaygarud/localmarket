package com.lmp.app.entity;

import java.util.ArrayList;
import java.util.List;

public class StoreInventoryV2 implements Inventory{

  private Item item;
  private List<StoreItem> stores = new ArrayList<>();
  
  public StoreInventoryV2(Item item, List<StoreItem> stores) {
    super();
    this.item = item;
    this.stores = stores;
  }
  
  public StoreInventoryV2(Item item, StoreItem storeItem) {
    super();
    this.item = item;
    this.stores.add(storeItem);
  }
  public Item getItem() {
    return item;
  }
  public void setItem(Item item) {
    this.item = item;
  }

  public List<StoreItem> getStores() {
    return stores;
  }

  public void setStores(List<StoreItem> stores) {
    this.stores = stores;
  }
  
  public void add(StoreItem item) {
    this.stores.add(item);
  }
}
