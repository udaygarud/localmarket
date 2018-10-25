package com.lmp.app.entity;

public enum OrderStatus {

  REVIEW("Review", 0),
  NEW("New", 1),
  CANCELLED("Cancelled", 1),
  IN_PROGRESS("In Progress", 2),
  READY("Ready", 3),
  SHIPPED("Shipped", 4),
  DELIVERED("Delivered", 5),
  COMPLETED("Completed", 6),
  RETURNED("Returned", 7);

  private OrderStatus(String name, int seq) {
    this.name = name;
  }

  private String name;
  private int seq;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getSeq() {
    return seq;
  }

  public void setSeq(int seq) {
    this.seq = seq;
  }
}
