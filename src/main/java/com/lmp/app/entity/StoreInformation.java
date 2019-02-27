package com.lmp.app.entity;

public class StoreInformation {
	private String storeId;
    private boolean onSale;
    private double offerPrice;
    private int stock;
    private boolean inStock;
    private String itemId;
    
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public boolean isOnSale() {
		return onSale;
	}
	public void setOnSale(boolean onSale) {
		this.onSale = onSale;
	}
	public double getOfferPrice() {
		return offerPrice;
	}
	public void setOfferPrice(double offerPrice) {
		this.offerPrice = offerPrice;
	}
	 public int getStock() {
		    return stock;
		  }
		  public void setStock(int stock) {
		    this.stock = stock;
		  }
    
		  public boolean isInStock() {
			    return inStock;
			  }
			  public void setInStock(boolean inStock) {
			    this.inStock = inStock;
			  }
}
