package com.lmp.app.entity;

import java.util.Collections;
import java.util.List;

public class CategoryTree {

  private CategoryNode root;

  public CategoryTree() {
    root = new CategoryNode("/", 0);
  }

  public void addPath(List<CategoryNode> list) {
    // sort root to leaf on priority
    Collections.sort(list);
    CategoryNode node = root;
    for (CategoryNode categoryNode : list) {
      node.addChild(categoryNode);
      node = categoryNode;
    }
  }

  public CategoryNode getRoot() {
    return root;
  }

  public void setRoot(CategoryNode root) {
    this.root = root;
  }
}

