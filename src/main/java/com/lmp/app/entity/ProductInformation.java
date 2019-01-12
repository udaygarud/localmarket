package com.lmp.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.lmp.db.pojo.ItemEntity;

public class ProductInformation {
	private ItemEntity item;
	  private List<String> stores = new ArrayList<>();
	public ItemEntity getItem() {
		return item;
	}
	public void setItem(ItemEntity item) {
		this.item = item;
	}
	public List<String> getStores() {
		return stores;
	}
	public void setStores(List<String> stores) {
		this.stores = stores;
	}
	
	
}
