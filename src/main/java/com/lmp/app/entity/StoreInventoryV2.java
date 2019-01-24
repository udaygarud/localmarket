package com.lmp.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.lmp.db.pojo.StoreItemEntity;

public class StoreInventoryV2 implements Inventory{

  private Item item;
  
  private List<StoreInformation> stores = new ArrayList<>();
  private boolean isPresentInWishList;
  
  public StoreInventoryV2(Item item, List<StoreInformation> stores,boolean ispresent) {
    super();
    this.item = item;
    this.stores = stores;
    this.isPresentInWishList = ispresent;
  }
  
  public StoreInventoryV2(Item item, StoreInformation store,boolean ispresent) {
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
  public List<StoreInformation> getStores() {
    return stores;
  }
  public void addStore(StoreInformation store) {
    this.stores.add(store);
  }
  public void setStores(List<StoreInformation> stores) {
    this.stores = stores;
  }
  
}
