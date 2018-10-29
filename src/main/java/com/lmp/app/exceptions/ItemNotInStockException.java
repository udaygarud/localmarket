package com.lmp.app.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ItemNotInStockException extends RuntimeException {

  private List<String> outOfStockItems = new ArrayList<>();

  public List<String> getOutOfStockItems() {
    return outOfStockItems;
  }

  public void setOutOfStockItems(List<String> outOfStockItems) {
    this.outOfStockItems = outOfStockItems;
  }
}
