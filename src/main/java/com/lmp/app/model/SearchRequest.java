package com.lmp.app.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.lmp.app.entity.FilterField;

public class SearchRequest extends PageableRequest {

  private String query;
  private String storeId;
  private Map<String, List<String>> filters = new HashMap<>();
  private List<String> fields = new ArrayList<>();
  private double lat;
  private double lng;
  private int radius = 5;
    
  public SearchRequest() {
  }
  public SearchRequest(int page, int count) {
    super(page, count);
  }

  public static SearchRequest createFor(String q, int page, int count) {
    SearchRequest sr = new SearchRequest(page, count);
    sr.query = q;
    return sr;
  }

  public static SearchRequest createSISearch(String storeId, String q, int page, int count) {
    SearchRequest sr = new SearchRequest(page, count);
    sr.query = q;
    sr.storeId = storeId;
    return sr;
  }

  public boolean isSolrSearchNeeded() {
    return !Strings.isNullOrEmpty(query) || brandFilter() != null || categoryFilter() != null
        || upcFilter() != null || priceFilter() != null;
  }

  public boolean isFilterOn() {
    return isOnSaleRequest() || brandFilter() != null || categoryFilter() != null
        || upcFilter() != null || priceFilter() != null;
  }

  public boolean isOnSaleRequest() {
    if(filters == null || !filters.containsKey(FilterField.ON_SALE.getValue())) {
      return false;
    }
    List<String> values = filters.get(FilterField.ON_SALE.getValue());
    return (values == null || values.isEmpty()) ? false : "true".equalsIgnoreCase(values.get(0));
  }

  public List<String> brandFilter() {
    if(filters == null || !filters.containsKey(FilterField.BRAND.getValue())) {
      return null;
    }
    return filters.get(FilterField.BRAND.getValue());
  }

  public List<String> categoryFilter() {
    if(filters == null || !filters.containsKey(FilterField.CATEGORY.getValue())) {
      return null;
    }
    return filters.get(FilterField.CATEGORY.getValue());
  }

  public String priceFilter() {
    if(filters == null || !filters.containsKey(FilterField.PRICE_RANGE.getValue())) {
      return null;
    }
    return filters.get(FilterField.PRICE_RANGE.getValue()).get(0).trim();
  }

  public List<String> upcFilter() {
    if(filters == null || !filters.containsKey(FilterField.UPC.getValue())) {
      return null;
    }
    return filters.get(FilterField.UPC.getValue());
  }

  public String getQuery() {
    return query == null ? null : query.trim().toLowerCase();
  }
  public void setQuery(String query) {
    this.query = query;
  }
  public Map<String, List<String>> getFilters() {
    return filters;
  }
  public void setFilters(Map<String, List<String>> filters) {
    this.filters = filters;
  }
  public List<String> getFields() {
    return fields;
  }
  public void setFields(List<String> fields) {
    this.fields = fields;
  }
  public String getStoreId() {
    return storeId == null ? null : storeId.trim().toLowerCase();
  }
  public void setStoreId(String storeId) {
    this.storeId = storeId;
  }
  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLng() {
    return lng;
  }

  public void setLng(double lng) {
    this.lng = lng;
  }

  public int getRadius() {
    return radius;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }
  
@Override
  public String toString() {
    return "query: " + query 
        + "storeId: " + storeId
        + "filters: " + filters == null ? "" : filters.toString()
        + "fields: " + fields == null ? "" : fields.toString();
  }
}
