package com.lmp.app.model.validator;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.base.Strings;
import com.lmp.app.entity.FilterField;
import com.lmp.app.entity.PriceRange;
import com.lmp.app.model.SearchRequest;
import com.lmp.solr.entity.ItemField;

@Component
public class SearchRequestValidator implements Validator {

  public boolean supports(Class clazz) {
    return SearchRequest.class.equals(clazz);
  }

  public void validate(Object obj, Errors e) {
    SearchRequest sRequest = (SearchRequest) obj;
    validateQueryAndStoreId(sRequest, e);
    validateFilters(sRequest, e);
    validateFields(sRequest, e);
  }

  private void validateFields(SearchRequest sRequest, Errors e) {
    if(sRequest.getFields() != null && sRequest.getFields().size() > 0) {
      for(String field : sRequest.getFields()) {
        try{
          ItemField.valueOf(field.trim().toUpperCase());
        } catch(IllegalArgumentException ex) {
          e.reject("field.invalid", "invalid field '" + field + "' in fields param");
        }
      }
    }
  }
  /**
   * either one of query or storeId is required
   * 
   * @param sr
   */
  private void validateQueryAndStoreId(SearchRequest sr, Errors e) {
    if (Strings.isNullOrEmpty(sr.getQuery()) && Strings.isNullOrEmpty(sr.getStoreId()) 
        && !sr.isFilterOn()) {
      e.reject("query_storeid.required", "either query, storeId or filter is required");
    }
  }

  private void validateFilters(SearchRequest sr, Errors e) {
    Map<String, List<String>> filters = sr.getFilters();
    if (filters != null && !filters.isEmpty()) {
      for (Entry<String, List<String>> filter : filters.entrySet()) {
        if (filter == null || filter.getKey() == null || filter.getValue() == null) {
          e.reject("field.invalid", "empty or null filter");
        } else {
          if (filter.getKey().equals(FilterField.ON_SALE.getValue())) {
            if (!(filter.getValue().get(0).equalsIgnoreCase("true") || filter.getValue().get(0).equalsIgnoreCase("false"))) {
              e.reject("field.invalid", "filter " + filter.getKey() + " can have either true or false");
            }
          } else if (filter.getKey().equals(FilterField.BRAND.getValue())) {
          } else if (filter.getKey().equals(FilterField.CATEGORY.getValue())) {
          } else if (filter.getKey().equals(FilterField.UPC.getValue())) {
          } else if (filter.getKey().equals(FilterField.PRICE_RANGE.getValue())) {
            if(filter.getValue() != null && filter.getValue().size() > 0) {
            	if(filter.getValue().get(0).contains("To")){
            		String[] tokens = filter.getValue().get(0).split("To");
                	if(tokens.length==2  && !Strings.isNullOrEmpty(tokens[0]) && !Strings.isNullOrEmpty(tokens[1])){
                		if(PriceRange.from(filter.getValue().get(0)) == null){
                			e.reject("field.invalid", "invalid value \""+ filter.getValue().get(0) +"\" for filter " + filter.getKey());
                		}        
                	}
                	else{
                		e.reject("field.invalid", "invalid filter: " + filter.getKey());
                	}
            	} 
            }
          } else {
            e.reject("field.invalid", "invalid filter: " + filter.getKey());
          }
        }
      }
    }
  }
  
  private boolean isNumber(String num) {
    try {
    Double.parseDouble(num);
    } catch(NumberFormatException e) {
      return false;
    }
    return true;
  }
}
