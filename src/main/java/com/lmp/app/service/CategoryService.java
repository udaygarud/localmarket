package com.lmp.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lmp.app.entity.CategoryNode;
import com.lmp.app.entity.CategoryTree;
import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.pojo.StoreEntity;

@Service
public class CategoryService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ItemService itemService;

  @Cacheable("catrgories")
  public Map<String, CategoryNode> buildProductCategorization() {
    int page = 0;
    int size = 5000;
    Map<String, CategoryNode> countMap = new HashMap<>();
    Set<Set<String>> categorySet = new HashSet<>();
    while(true) {
      logger.info("requesting for documents page {} and size{}", page, size);
      Iterable<ItemEntity> items = itemService.getAllDocs(page, size);
      if(items == null || !items.iterator().hasNext()) {
        logger.info("finished pulling all item docs");
        break;
      }
      logger.info("received documents from page: {}", page);
      Iterator<ItemEntity> it = items.iterator();
      while(it.hasNext()) {
        ItemEntity item = it.next();
        for(String cat : item.getCategories()) {
         // cat = cat.toLowerCase().trim();
        	cat = cat.trim();
          // count the occurance of the category name
          if(countMap.containsKey(cat)) {
            countMap.put(cat, countMap.get(cat).incrPriority());
          } else {
            countMap.put(cat, new CategoryNode(cat, 1));
          }
        }
        // put item.getCategories() in set
        categorySet.add(item.getCategories());
      }
      //go for next page
      page++;
    }
    CategoryTree cTree = new CategoryTree();
    for(Set<String> catSet : categorySet) {
      // get the max occurance node from the set and set that as root and set second max as its child
      List<CategoryNode> list = new ArrayList<>();
      catSet.forEach(cat -> {
        list.add(countMap.get(cat.trim()));
      });
      cTree.addPath(list);
    }
    countMap.put("root", cTree.getRoot());
    return countMap;
  }

  /**
   * get categories for the filter.If request comes with single category then 
   * show all sub categories in the filter. If request has multiple categories in filter then
   * show root level categories.
   * If no category specified then get categories from the stores around.
   * @param category
   * @param stores
   * @return
   */
  public List<String> getCategories(List<String> category, Iterable<StoreEntity> stores) {
    Set<String> set = new HashSet<>();
    if(category != null && category.size() == 1) {
      CategoryNode root = buildProductCategorization().get(category.get(0).toLowerCase().trim());
      for (CategoryNode node : root.getSubCategories()) {
        set.add(node.getName());
      }
    } else {
      // get all categories for all the stores
      for (StoreEntity store : stores) {
        if(store.getCapabilities() != null) {
          set.addAll(store.getCapabilities().getListedCategories());
        }
      }
    }
    List<String> cats = new ArrayList<>();
    cats.addAll(set);
    Collections.sort(cats);
    return cats;
  }
}
