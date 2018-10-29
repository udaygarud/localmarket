package com.lmp.app.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import com.google.common.collect.Lists;
import com.lmp.app.entity.CustomerOrder;
import com.lmp.app.entity.Inventory;
import com.lmp.app.entity.Item;
import com.lmp.app.entity.StoreInventory;
import com.lmp.app.entity.StoreInventoryV2;
import com.lmp.app.service.StoreInventoryService;
import com.lmp.db.pojo.CustomerOrderEntity;
import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.pojo.StoreItemEntity;
import com.lmp.solr.entity.ItemDoc;

public class SearchResponse<T> extends BaseResponse {
  @Autowired
  private static StoreInventoryService storeService;
  private Collection<T> results;
  private long found;
  private int page;
  private int rows;

  private SearchResponse<T> blank() {
    
    this.statusCode = HttpStatus.OK.value();
    return this;
  }

  public static SearchResponse<CustomerOrder> buildOrderResponse(Page<CustomerOrderEntity> page) {
    SearchResponse<CustomerOrder> response = new SearchResponse<>();
    response.statusCode = HttpStatus.OK.value();
    response.found = page.getTotalElements();
    response.page = page.getPageable().getPageNumber();
    response.rows = page.getPageable().getPageSize();
    response.results = CustomerOrderEntity.toCustomerOrderList(page.getContent());
    return response;
  }
  private static Map<String, Inventory> buildStoreItemMap(List<StoreItemEntity> items,Map<String,List<String>> stores) {
    Map<String, Inventory> map = new HashMap<>();
    for(StoreItemEntity ie : items) {
      Item item = Item.fromStoreInventoryEntity(ie);
      if(map.containsKey(ie.getStoreId())) {
        ((StoreInventory)map.get(ie.getStoreId())).getItems().add(item);
      } else {
        List<Item> list = new ArrayList<>();
        list.add(item);
        map.put(ie.getStoreId(), new StoreInventory(ie.getStoreId(), list));
      }
    }
    return map;
  }

  private static Map<String, Inventory> buildStoreItemMapV2(List<StoreItemEntity> items,Map<String,List<String>> stores) {
    Map<String, Inventory> map = new HashMap<>();
    for(StoreItemEntity ie : items) {
      Item item = Item.fromStoreInventoryEntity(ie);
     
      List<String> eachitem = stores.get(item.getId());
      
      eachitem.forEach(storerecord -> {
       
        if(map.containsKey(ie.getId())){
          ((StoreInventoryV2)map.get(ie.getId())).addStore(storerecord);
        }else{
          map.put(item.getId(), new StoreInventoryV2(item, ie.getStoreId()));
        }
      });

      /* if(map.containsKey(item.getId())) {
        List<String> eachitem = stores.get(item.getId());
        eachitem.forEach(storerecord -> {
          ((StoreInventoryV2)map.get(ie.getStoreId())).addStore(storerecord);
        });
       
      } else {
        map.put(item.getId(), new StoreInventoryV2(item, ie.getStoreId()));
      } */
    }
    return map;
  }

  public static SearchResponse<ItemEntity> buildItemResponse(Page<ItemDoc> result, Iterable<ItemEntity> items) {
    SearchResponse<ItemEntity> response = new SearchResponse<>();
    response.statusCode = HttpStatus.OK.value();
    response.found = result.getTotalElements();
    response.page = result.getPageable().getPageNumber();
    response.rows = result.getPageable().getPageSize();
    response.results = Lists.newArrayList(items);
    return response;
  }

  public static SearchResponse<Inventory> buildStoreInventoryResponse(Page<StoreItemEntity> page, boolean v2,Map<String,List<String>> stores) {
    if(page == null || !page.hasContent()) {
      SearchResponse<Inventory> blank = new SearchResponse<>();
      return blank.blank();
    }
    SearchResponse<Inventory> response = new SearchResponse<>();
    response.statusCode = HttpStatus.OK.value();
    response.found = page.getTotalElements();
    response.page = page.getPageable().getPageNumber();
    response.rows = page.getPageable().getPageSize();
    if(v2) {
      response.results = buildStoreItemMapV2(page.getContent(),stores).values();
    } else {
      response.results = buildStoreItemMap(page.getContent(),stores).values();
    }
    return response;
  }

  public static SearchResponse<? super Inventory> buildStoreInventoryResponse(Page<StoreItemEntity> page, long count, boolean v2,Map<String,List<String>> stores) {
    SearchResponse<? super Inventory> response = buildStoreInventoryResponse(page, v2,stores);
    response.found = count;
    return response;
  }
  public static SearchResponse<? super Inventory> buildStoreInventoryResponse(Page<StoreItemEntity> page, long count, int pageNo, boolean v2,Map<String,List<String>> stores) {
    SearchResponse<? super Inventory> response = buildStoreInventoryResponse(page, count, v2,stores);
    response.page = pageNo;
    return response;
  }  

  public Collection<T> getResults() {
    return results;
  }

  public void setResults(List<T> results) {
    this.results = results;
  }

  public long getFound() {
    return found;
  }
  public void setFound(long found) {
    this.found = found;
  }
  public int getPage() {
    return page;
  }
  public void setPage(int page) {
    this.page = page;
  }
  public int getRows() {
    return rows;
  }
  public void setRows(int rows) {
    this.rows = rows;
  }
}
