package com.lmp.app.model;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmp.db.pojo.Currency;
import com.lmp.db.pojo.UserEntity;

import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import org.springframework.util.Assert;
@Component
public class NewStoreUserRequest {

  private String firstName;
  private String lastName;
  private String userName;
  private String email;
  private String phoneNumber;
  private Address address;

  public Address getAddress() {
    return this.address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }
  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getUserName() {
    return this.userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  
  
  public NewStoreUserRequest() {
  }

}

class Address {
  private String line1;
  private String line2;
  private String city;
  private String state;
  private String country;
  private int zip;
  public String getLine1() {
    return line1;
  }
  public void setLine1(String line1) {
    this.line1 = line1;
  }
  public String getLine2() {
    return line2;
  }
  public void setLine2(String line2) {
    this.line2 = line2;
  }
  public String getCity() {
    return city;
  }
  public void setCity(String city) {
    this.city = city;
  }
  public String getState() {
    return state;
  }
  public void setState(String state) {
    this.state = state;
  }
  public String getCountry() {
    return country;
  }
  public void setCountry(String country) {
    this.country = country;
  }
  public int getZip() {
    return zip;
  }
  public void setZip(int zip) {
    this.zip = zip;
  }
}