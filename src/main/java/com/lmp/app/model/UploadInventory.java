package com.lmp.app.model;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmp.db.pojo.Currency;

@Component
public class UploadInventory {

  

  private String storeId;
  private long upc;
  private double listPrice;
  private int stock;

 
  public UploadInventory() {
  }

  public double getListPrice() {
      return listPrice;
  }
  
  public void setListPrice(double listPrice) {
      this.listPrice = listPrice;
  }
  
  public int getStock() {
      return stock;
  }
  public void setStock(int stock) {
      this.stock = stock;
  }
  public String getStoreId() {
      return storeId;
  }
  public void setStoreId(String storeId) {
      this.storeId = storeId;
  }
  public long getUpc() {
      return upc;
  }
  public void setUpc(long upc) {
      this.upc = upc;
  }

  @Override
  public String toString() {
    return "listPrice: " +  listPrice 
        + "storeId: " + storeId
        + "upc: " + upc
        + "stock: " + stock;
  }

}

