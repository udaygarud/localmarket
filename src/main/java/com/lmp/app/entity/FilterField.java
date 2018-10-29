package com.lmp.app.entity;

public enum FilterField {

  ON_SALE("onsale"), BRAND("brand"), CATEGORY("cat"), UPC("upc"), PRICE_RANGE("price");

  private String value;

  private FilterField(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
