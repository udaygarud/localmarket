package com.lmp.db.pojo;

import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.lmp.app.entity.ShoppingCart;
import com.lmp.app.model.StoreRequest;

@Document(collection="store")
public class StoreEntity {

  @Id
  private String id;
  private String name;
  private String franchise;
  @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
  @Nullable
  private Location location;
  private String address;
  @Nullable
  private UserEntity storeOwner;
  private StoreCapabilities capabilities;

  public static StoreEntity toEntity(StoreRequest request) {
    StoreEntity entity = new StoreEntity();
    BeanUtils.copyProperties(request, entity);
    StoreCapabilities scEntity = new StoreCapabilities();
    BeanUtils.copyProperties(request.getCapabilities(), scEntity);
    entity.setCapabilities(scEntity);
    return entity;
  }
  
  public String getId() {
    return id;
  }
  public StoreEntity setId(String id) {
    this.id = id;
    return this;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public UserEntity getStoreOwner() {
    return storeOwner;
  }
  public void setStoreOwner(UserEntity storeOwner) {
    this.storeOwner = storeOwner;
  }
  public String getFranchise() {
    return franchise;
  }
  public void setFranchise(String franchise) {
    this.franchise = franchise;
  }
  public Location getLocation() {
    return location;
  }
  public void setLocation(Location location) {
    this.location = location;
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
  public void setCapabilities(StoreCapabilities capabilities) {
    this.capabilities = capabilities;
  }

  /**
   * currency type
   * inventory categories
   * store times
   * liquor sold?
   * cooked food served?
   * @author skawale
   *
   */
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
}

class Location {
  private String type;
  private double[] coordinates;
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public double[] getCoordinates() {
    return coordinates;
  }
  public void setCoordinates(double[] coordinates) {
    if(coordinates == null || coordinates.length < 2) {
      Assert.notNull(coordinates, "coordinates must not be null or less than 2!");
    }
    if(coordinates[0] > coordinates[1]) {
      double temp = coordinates[0];
      coordinates[0] = coordinates[1];
      coordinates[1] = temp;
    }
    this.coordinates = coordinates;
  }
}
