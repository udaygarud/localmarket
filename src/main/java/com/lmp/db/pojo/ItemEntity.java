package com.lmp.db.pojo;

import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

@Document(collection="item")
public class ItemEntity {

  @Id
  private String id;
  private Set<String> categories;
  private String title;
  private String url;
  private String brand;
  @Indexed(unique = true)
  private long upc;
  private long tcin;
  private String dpci;
private float list_price;
  private float offer_price;
  private long release_date;
  private List<String> bullet_description;
  private SoftBullets soft_bullets;
  private String description;
  private String package_dimensions;
  @JsonIgnore
  private String available_to_purchase_date_time;
  private List<Image> images;

  /**
   * check if item can be listed on store inventory
   * it does check whether item is of the category which store can list
   * @param store
   * @return
   */
  public boolean canGoOnStoreInventory(StoreEntity store) {
    Set<String> cats = MoreObjects.firstNonNull(
        store.getCapabilities().getListedCategories(), ImmutableSet.<String>of());
    if(categories == null || categories.isEmpty() 
        || cats.isEmpty()) {
      return false;
    }
    for(String cat : cats) {
      if(categories.contains(cat)) {
        return true;
      }
    }
    return false;
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
  public float getList_price() {
    return list_price;
  }
  public void setList_price(float list_price) {
    this.list_price = list_price;
  }
  public float getOffer_price() {
    return offer_price;
  }
  public void setOffer_price(float offer_price) {
    this.offer_price = offer_price;
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
