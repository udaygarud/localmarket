package com.lmp.app.entity;

import java.util.ArrayList;
import java.util.List;

public class StoreInventoryV2 implements Inventory{

  private Item item;
  private List<String> stores = new ArrayList<>();
  private boolean isPresentInWishList;
  
  public StoreInventoryV2(Item item, List<String> stores,boolean ispresent) {
    super();
    this.item = item;
    this.stores = stores;
    this.isPresentInWishList = ispresent;
  }
  
  public StoreInventoryV2(Item item, String store,boolean ispresent) {
    super();
    this.item = item;
    this.stores.add(store);
    this.isPresentInWishList = ispresent;
  }
  public Item getItem() {
    return item;
  }
  public boolean isIspresentinWishList() {
	return isPresentInWishList;
}

public void setIspresentinWishList(boolean ispresentinWishList) {
	this.isPresentInWishList = ispresentinWishList;
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
