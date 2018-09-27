package com.lmp.app.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

/**
 * Category Tree data structure
 * @author skawale
 *
 */
public class CategoryNode implements Comparable<CategoryNode>{

  private String name;
  private int priority;
  private List<CategoryNode> subCategories;

  public CategoryNode(String name, int priority) {
    this.name = name;
    this.priority = priority;
    this.subCategories = new ArrayList<>();
  }

  public void addChild(CategoryNode cat) {
    if(!subCategories.contains(cat) && 
        !cat.name.equalsIgnoreCase(this.name)) {// to avoid the infinite loop
      subCategories.add(cat);
    }
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public CategoryNode incrPriority() {
    priority++;
    return this;
  }
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<CategoryNode> getSubCategories() {
    return subCategories;
  }

  public void setSubCategories(List<CategoryNode> subCategories) {
    this.subCategories = subCategories;
  }

  @Override
  public int compareTo(CategoryNode o) {
    return Integer.compare(o.priority, this.priority);
  }

  @Override
  public boolean equals(Object o) {
    CategoryNode node = (CategoryNode) o;
    return this.name.equalsIgnoreCase(node.name);
  }
  @Override
  public int hashCode() {
    return this.name.hashCode();
  }
}
