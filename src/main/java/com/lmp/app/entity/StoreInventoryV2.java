package com.lmp.app.entity;

import java.util.ArrayList;
import java.util.List;

public class StoreInventoryV2 implements Inventory{

  private Item item;
  private List<String> stores = new ArrayList<>();
  
  public StoreInventoryV2(Item item, List<String> stores) {
    super();
    this.item = item;
    this.stores = stores;
  }
  
  public StoreInventoryV2(Item item, String store) {
    super();
    this.item = item;
    this.stores.add(store);
  }
  public Item getItem() {
    return item;
  }
  public void setItem(Item item) {
    this.item = item;
  }
  public List<String> getStores() {
    return stores;
  }
  public void addStore(String store) {
    this.stores.add(store);
  }
  public void setStores(List<String> stores) {
    this.stores = stores;
  }
  
}
