package com.lmp.solr.entity;

public enum ItemField {

  ID("id"), CONTENT("content"), TITLE("title"), BRAND("brand"), CATEGORIES("categories"), UPC("upc"),
  MIN_PRICE("minPrice"), MAX_PRICE("maxPrice"), STORES("stores");

  private String value;

  ItemField(String inValue) {
    value = inValue;
  }

  public String getValue() {
    return value;
  }
}
