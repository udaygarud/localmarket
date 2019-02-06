package com.lmp.solr.entity;

import java.util.Set;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.lmp.db.pojo.ItemEntity;

@SolrDocument(solrCoreName = "itemdoc")
public class ItemDoc {

  @Id
  @Field("id")
  private String id;
  @Field("brand")
  private String brand;
  @Field("categories")
  private String [] categories;
  @Field("content")
  private String content;
  @Field("upc")
  private String upc;
  @Field("stores")
  private String stores;
  @Field("minPrice")
  private float minPrice;
  @Field("maxPrice")
  private float maxPrice;


  public static ItemDoc fromItem(ItemEntity item, String storeids) {
    if(item == null) {
      return null;
    }
    ItemDoc itemDoc = new ItemDoc();
    Joiner joiner = Joiner.on(" ").skipNulls();
    itemDoc.id = item.getId();
    itemDoc.upc = Strings.nullToEmpty(Long.toString(item.getUpc()));
    itemDoc.brand = Strings.nullToEmpty(item.getBrand());
    itemDoc.setStores(storeids);
    if(item.getCategories() != null) {
      //itemDoc.categories = joiner.join(item.getCategories());
    	itemDoc.categories = convert(item.getCategories());
    }
    itemDoc.content = itemDoc.brand + " " + itemDoc.categories 
        + " " + item.getTitle();
    return itemDoc;
  }

  private ItemDoc() {
  }

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getBrand() {
    return brand;
  }
  public void setBrand(String brand) {
    this.brand = brand;
  }
  public String[] getCategories() {
    return categories;
  }
  public void setCategories(String[] categories) {
    this.categories = categories;
  }
  public String getContent() {
    return content;
  }
  public void setContent(String content) {
    this.content = content;
  }
  public float getMinPrice() {
    return minPrice;
  }

  public ItemDoc setMinPrice(float minPrice) {
    this.minPrice = minPrice;
    return this;
  }

  public float getMaxPrice() {
    return maxPrice;
  }

  public ItemDoc setMaxPrice(float maxPrice) {
    this.maxPrice = maxPrice;
    return this;
  }

  public String getUpc() {
    return upc;
  }

  public void setUpc(String upc) {
    this.upc = upc;
  }

  public String getStores() {
    return stores;
  }

  public void setStores(String stores) {
    this.stores = stores;
  }
  public static String[] convert(Set<String> setOfString) 
  { 

      // Create String[] of size of setOfString 
      String[] arrayOfString = new String[setOfString.size()]; 

      // Copy elements from set to string array 
      // using advanced for loop 
      int index = 0; 
      for (String str : setOfString) 
          arrayOfString[index++] = str; 

      // return the formed String[] 
      return arrayOfString; 
  } 
}
