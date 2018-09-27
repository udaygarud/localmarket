package com.lmp.db.pojo;

public enum Currency {
  USD("$"), INR("Rs. ");
  
  private String value;
  private Currency(String val) {
    this.value = val;
  }

  public String getValue() {
    return value;
  }
}