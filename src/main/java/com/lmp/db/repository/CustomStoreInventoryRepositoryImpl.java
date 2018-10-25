package com.lmp.db.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import com.lmp.app.model.ResponseFilter;

public class CustomStoreInventoryRepositoryImpl {

  @Autowired
  private MongoTemplate mongoTemplate;

  private Criteria or(List<String> ids, String fieldName) {
    if(ids == null || ids.isEmpty()) {
      return null;
    }
    Criteria criteria = Criteria.where(fieldName).is(ids.get(0));
    for(int i = 1; i < ids.size(); i++) {
      criteria.orOperator(Criteria.where(fieldName).is(ids.get(i)));
    }
    return criteria;
  }

  private Criteria and(List<String> ids, String fieldName) {
    if(ids == null || ids.isEmpty()) {
      return null;
    }
    Criteria criteria = Criteria.where(fieldName).is(ids.get(0));
    for(int i = 1; i < ids.size(); i++) {
      criteria.andOperator(Criteria.where(fieldName).is(ids.get(i)));
    }
    return criteria;
  }

//  private ResponseFilter facetOnPriceGroup(Criteria criteria) {
//    Aggregation agg = newAggregation(
//        match(criteria),
//        group("salePrice").count().as("count"),
//        project("count").and("salePrice").previousOperation(),
//        sort(Sort.Direction.ASC, "salePrice")
//      );
//    AggregationResults<PriceGroupCount> groupResults 
//    = mongoTemplate.aggregate(agg, StoreItemEntity.class, PriceGroupCount.class);
//    List<PriceGroupCount> result = groupResults.getMappedResults();
//    Map<PriceGroup, Integer> map = new HashMap<>();
//    int currentGroup = 0;
//    for (PriceGroupCount priceCount : result) {
//      PriceGroup pg = PriceGroup.orderMap.get(currentGroup);
//      while(pg.getMax() != 0 && pg.getMax() < (int)priceCount.getPrice()) {
//        currentGroup++;
//        pg = PriceGroup.orderMap.get(currentGroup);
//      }
//      if(map.containsKey(pg)) {
//        map.put(pg, map.get(pg) + 1);
//      } else {
//        map.put(pg, 1);
//      }
//    }
//    
//    return ResponseFilter.fromMap("price", map);
//  }

  public List<ResponseFilter> facetSearch(List<String> storeIds, List<String> itemIds, boolean onSale) {
    Criteria criteria = new Criteria();
    if(storeIds != null && !storeIds.isEmpty()) {
      criteria = or(storeIds, "storeId");
    }
    if(itemIds != null && !itemIds.isEmpty()) {
      criteria.andOperator(or(itemIds, "item.id"));
    }
    if(onSale) {
      criteria.andOperator(Criteria.where("onSale").is(true));
    }

    List<ResponseFilter> filters = new ArrayList<>();
    //filters.add(facetOnPrice(criteria));
    return filters;
  }

  
}
