package com.lmp.solr.entity;

import java.util.List;

import org.springframework.data.solr.core.query.Criteria;

public class QueryUtils {

  public static Criteria orQuery(ItemField field, List<String> searchTerm) {
    if(searchTerm == null || searchTerm.isEmpty()) {
      return null;
    }
    Criteria condition = null;
    for(String term : searchTerm) {
      if(condition == null) {
        condition = new Criteria(field.getValue()).contains(term); 
      } else {
        condition = condition.or(new Criteria(field.getValue()).contains(term));
      }
    }
    return condition;
  }

  /*
  * Split on white space and create a OR query
  */
  public static Criteria orQuery(ItemField field, String searchTerm) {
    if(searchTerm == null || searchTerm.isEmpty()) {
      return null;
    }
    Criteria condition = null;
    for(String term : searchTerm.split(" ")) {
      if(condition == null) {
        condition = new Criteria(field.getValue()).contains(term); 
      } else {
        condition = condition.or(new Criteria(field.getValue()).contains(term));
      }
    }
    return condition;
  }

  public static Criteria andQuery(ItemField field, List<String> searchTerm) {
    if(searchTerm == null || searchTerm.isEmpty()) {
      return null;
    }
    Criteria condition = null;
    for(String term : searchTerm) {
      if(condition == null) {
        condition = new Criteria(field.getValue()).contains(term); 
      } else {
        condition = condition.and(new Criteria(field.getValue()).contains(term));
      }
    }
    return condition;
  }

  public static Criteria oRIsQuery(ItemField field, List<String> searchTerm) {
    if(searchTerm == null || searchTerm.isEmpty()) {
      return null;
    }
    Criteria condition = null;
    for(String term : searchTerm) {
      if(condition == null) {
        condition = new Criteria(field.getValue()).is(term); 
      } else {
        condition = condition.or(new Criteria(field.getValue()).is(term));
      }
    }
    return condition;
  }
  /*
  * Split on white space and create an AND query
  */
  public static Criteria andQuery(ItemField field, String searchTerm) {
    if(field == null) {
      return null;
    }
    return andQuery(field.getValue(), searchTerm);
  }

  public static Criteria andQuery(String field, String searchTerm) {
    if(searchTerm == null || searchTerm.isEmpty()) {
      return null;
    }
    Criteria condition = null;
    for(String term : searchTerm.split(" ")) {
      if(condition == null) {
        condition = new Criteria(field).contains(term); 
      } else {
        condition = condition.and(new Criteria(field).contains(term));
      }
    }
    return condition;
  }

  public static Criteria between(ItemField field, int from, int to) {
    if(field == null) {
      return null;
    }
    if(to == 0) {
    	return new Criteria(field.getValue()).greaterThanEqual(from);
    } else {
      return new Criteria(field.getValue()).between(from, to);
    }
  }
}
