package com.lmp.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.lmp.db.pojo.ItemEntity;

public class ProductInformation {
	private ItemEntity item;
	  private List<StoreInformation> stores = new ArrayList<>();
	public ItemEntity getItem() {
		return item;
	}
	public void setItem(ItemEntity item) {
		this.item = item;
	}
	public List<StoreInformation> getStores() {
		return stores;
	}
	public void setStores(List<StoreInformation> stores) {
		this.stores = stores;
	}
	
	
}
