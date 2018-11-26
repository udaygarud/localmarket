package com.lmp.app.model;

import java.util.Set;
import com.lmp.db.pojo.Currency;

public class StoreProfile {
	  private String storeId;
	  private String userId;
	  private String name;
	  private String franchise;
	  private String address;
	  private StoreCapabilities capabilities;
	  private Location location;  
	  private String phoneNumber;
	
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFranchise() {
		return franchise;
	}
	public void setFranchise(String franchise) {
		this.franchise = franchise;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public StoreCapabilities getCapabilities() {
		return capabilities;
	}
	
	public static class StoreCapabilities {
	    private boolean liquorSold;
	    private boolean foodServed;
	    private Set<String> listedCategories;
	    private Currency currency;
	    public boolean isLiquorSold() {
	      return liquorSold;
	    }
	    public void setLiquorSold(boolean liquorSold) {
	      this.liquorSold = liquorSold;
	    }
	    public boolean isFoodServed() {
	      return foodServed;
	    }
	    public void setFoodServed(boolean foodServed) {
	      this.foodServed = foodServed;
	    }
	    public Set<String> getListedCategories() {
	      return listedCategories;
	    }
	    public void setListedCategories(Set<String> listedCategories) {
	      this.listedCategories = listedCategories;
	    }
	    public Currency getCurrency() {
	      return currency;
	    }
	    public void setCurrency(Currency currency) {
	      this.currency = currency;
	    }
	  }
	public void setCapabilities(StoreCapabilities capabilities) {
		this.capabilities = capabilities;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
      
}
