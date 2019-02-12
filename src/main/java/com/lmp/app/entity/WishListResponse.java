package com.lmp.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.lmp.app.entity.ShoppingWishList.WishItem;

public class WishListResponse {
	 private WishItem item;
	  
	  private List<StoreInformation> stores = new ArrayList<>();
	  private boolean isPresentInWishList;
	  public WishListResponse(WishItem item, List<StoreInformation> stores,boolean ispresent) {
		    super();
		    this.item = item;
		    this.stores = stores;
		    this.isPresentInWishList = ispresent;
		  }
		  
		  public WishListResponse(WishItem item, StoreInformation store,boolean ispresent) {
		    super();
		    this.item = item;
		    this.stores.add(store);
		    this.isPresentInWishList = ispresent;
		  }

		public WishItem getWishItem() {
			return item;
		}

		public void setWishItem(WishItem item) {
			this.item = item;
		}

		public List<StoreInformation> getStores() {
			return stores;
		}

		public void setStores(List<StoreInformation> stores) {
			this.stores = stores;
		}

		public boolean isPresentInWishList() {
			return isPresentInWishList;
		}

		public void setPresentInWishList(boolean isPresentInWishList) {
			this.isPresentInWishList = isPresentInWishList;
		}
		  
}
