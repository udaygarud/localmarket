package com.lmp.app.entity;

import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lmp.db.pojo.StoreItemEntity;

public class Item {

  private String id;
  private int stock;
  private Set<String> categories;
  private String title;
  private String url;
  private String brand;
  private long upc;
  private long tcin;
  private String dpci;
  private double listPrice;
  private double offerPrice;
  private boolean onSale;
  private boolean inStock;
  private long release_date;
  private List<String> bullet_description;
  private SoftBullets soft_bullets;
  private String description;
  private String package_dimensions;
  private String available_to_purchase_date_time;
  private List<Image> images;

  public static Item fromStoreInventoryEntity(StoreItemEntity itemEnyity) {
    Item item = new Item();
    BeanUtils.copyProperties(itemEnyity.getItem(), item);
    item.stock = itemEnyity.getStock();
    item.id = itemEnyity.getId();
    item.onSale = itemEnyity.isOnSale();
    item.inStock = itemEnyity.getStock() > 0;
    item.listPrice = Math.round(itemEnyity.getListPrice() * 100.0) / 100.0;
    item.offerPrice = Math.round(itemEnyity.getSalePrice() * 100.0) / 100.0;
    return item;
  }
  public static Item fromStoreInventoryV2Entity(StoreItemEntity itemEnyity) {
    Item item = new Item();
    BeanUtils.copyProperties(itemEnyity.getItem(), item);
    
    return item;
  }
  public int getStock() {
    return stock;
  }
  public void setStock(int stock) {
    this.stock = stock;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public String getBrand() {
    return brand;
  }
  public void setBrand(String brand) {
    this.brand = brand;
  }
  public long getUpc() {
    return upc;
  }
  public void setUpc(long upc) {
    this.upc = upc;
  }
  public long getTcin() {
    return tcin;
  }
  public void setTcin(long tcin) {
    this.tcin = tcin;
  }
  public String getDpci() {
    return dpci;
  }
  public void setDpci(String dpci) {
    this.dpci = dpci;
  }
  public long getRelease_date() {
    return release_date;
  }
  public void setRelease_date(long release_date) {
    this.release_date = release_date;
  }
  public List<String> getBullet_description() {
    return bullet_description;
  }
  public void setBullet_description(List<String> bullet_description) {
    this.bullet_description = bullet_description;
  }
  public SoftBullets getSoft_bullets() {
    return soft_bullets;
  }
  public void setSoft_bullets(SoftBullets soft_bullets) {
    this.soft_bullets = soft_bullets;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getPackage_dimensions() {
    return package_dimensions;
  }
  public void setPackage_dimensions(String package_dimensions) {
    this.package_dimensions = package_dimensions;
  }
  public String getAvailable_to_purchase_date_time() {
    return available_to_purchase_date_time;
  }
  public void setAvailable_to_purchase_date_time(String available_to_purchase_date_time) {
    this.available_to_purchase_date_time = available_to_purchase_date_time;
  }
  public List<Image> getImages() {
    return images;
  }
  public void setImages(List<Image> images) {
    this.images = images;
  }
  public Set<String> getCategories() {
    return categories;
  }
  public void setCategories(Set<String> categories) {
    this.categories = categories;
  }
  public double getListPrice() {
    return listPrice;
  }
  public void setListPrice(double listPrice) {
    this.listPrice = listPrice;
  }
  public double getOfferPrice() {
    return offerPrice;
  }
  public void setOfferPrice(double offerPrice) {
    this.offerPrice = offerPrice;
  }
  public boolean isOnSale() {
    return onSale;
  }
  public void setOnSale(boolean onSale) {
    this.onSale = onSale;
  }
  public boolean isInStock() {
    return inStock;
  }
  public void setInStock(boolean inStock) {
    this.inStock = inStock;
  }
}

class SoftBullets {
  private String title;
  private List<String> bullets;
  public SoftBullets() { }
  public SoftBullets(String empty) { this.title = empty;}
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public List<String> getBullets() {
    return bullets;
  }
  public void setBullets(List<String> bullets) {
    this.bullets = bullets;
  }

}

@JsonIgnoreProperties(ignoreUnknown = true)
class Image {

  public String base_url;
  public String primary;
  @JsonIgnore
  public List<String> alternate_urls;
  public String getBase_url() {
    return base_url;
  }
  public void setBase_url(String base_url) {
    this.base_url = base_url;
  }
  public String getPrimary() {
    return primary;
  }
  public void setPrimary(String primary) {
    this.primary = primary;
  }
  public List<String> getAlternate_urls() {
    return alternate_urls;
  }
  public void setAlternate_urls(List<String> alternate_urls) {
    this.alternate_urls = alternate_urls;
  }

}
