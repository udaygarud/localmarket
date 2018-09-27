package com.lmp.app.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;

import com.lmp.db.pojo.StoreEntity;
import com.lmp.solr.entity.ItemDoc;
import com.lmp.solr.entity.ItemField;

public class ResponseFilter {

  private String filterName;
  private List<CountPair> values = new ArrayList<>();

  public ResponseFilter() {
  }

  public ResponseFilter(String name) {
    filterName = name;
  }

  private static class Store {
    public String id;
    public String name;
    public String address;
    public Store fromStoreEntity(StoreEntity entity) {
      this.id = entity.getId();
      this.name = entity.getName();
      this.address = entity.getAddress();
      return this;
    }
  }
  public static ResponseFilter buildStoreFilter(String fieldName, Iterable<StoreEntity> stores) {
    ResponseFilter response = new ResponseFilter(fieldName);
    for (StoreEntity store : stores) {
      response.values.add(new CountPair(new Store().fromStoreEntity(store), 1L));
    }
    return response;
  }

  public static ResponseFilter booleanFilter(String fieldName) {
    ResponseFilter response = new ResponseFilter(fieldName);
    response.values.add(new CountPair("true", 1L));
    response.values.add(new CountPair("false", 1L));
    return response;
  }

  public static ResponseFilter fromList(String fieldName, List<String> categories) {
    ResponseFilter response = new ResponseFilter(fieldName);
    for (String category : categories) {
      response.values.add(new CountPair(category, 1L));
    }
    return response;
  }

  public static List<ResponseFilter> buildResultFilter(List<ItemField> fieldNames, FacetPage<ItemDoc> docs) {
    List<ResponseFilter> response = new ArrayList<>();
    for (ItemField fieldName : fieldNames) {
      response.add(buildResultFilter(fieldName.getValue(), docs));
    }
    return response;
  }

  public static ResponseFilter buildResultFilter(String fieldName, FacetPage<ItemDoc> docs) {
    ResponseFilter response = new ResponseFilter();
    response.filterName = fieldName;
    if(docs != null && docs.getFacetResultPage(fieldName) != null) {
      Page<FacetFieldEntry> page = docs.getFacetResultPage(fieldName);
      page.getContent().forEach(entry -> {
        response.values.add(new CountPair(entry.getValue(), entry.getValueCount()));
      });
    }
    return response;
  }
  public String getFilterName() {
    return filterName;
  }
  public void setFilterName(String filterName) {
    this.filterName = filterName;
  }
  public List<CountPair> getValue() {
    return values;
  }
  public void setValue(List<CountPair> value) {
    this.values = value;
  }
}

class CountPair {
  private Object name;
  private Long count;

  public CountPair(Object name, Long count) {
    this.name = name;
    this.count = count;
  }

  public Object getName() {
    return name;
  }

  public void setName(Object name) {
    this.name = name;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }
}
