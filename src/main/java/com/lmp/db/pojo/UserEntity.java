package com.lmp.db.pojo;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.lmp.app.entity.Customer;

@Document(collection="user")
public class UserEntity {

  @Id
  private String id;
  private String firstName;
  private String lastName;
  private String userName;
  private String email;
  private String phoneNumber;
  private Address address;

  public static UserEntity fromPojo(Customer customer) {
    UserEntity entity = new UserEntity();
    BeanUtils.copyProperties(customer, entity);
    entity.setUserName(customer.getId());
    return entity;
  }
  public String getId() {
    return id;
  }
  public UserEntity setId(String id) {
    this.id = id;
    return this;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public String getUserName() {
    return userName;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getPhoneNumber() {
    return phoneNumber;
  }
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  public Address getAddress() {
    return address;
  }
  public void setAddress(Address address) {
    this.address = address;
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