package com.lmp.db.repository;

import java.util.List;

import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;

import com.lmp.app.model.ResponseFilter;
import com.lmp.db.pojo.StoreItemEntity;

public interface CustomStoreInventoryRepository {

  public List<StoreItemEntity> search(SimpleQuery query);
 // public List<ResponseFilter> facetSearch(List<String> storeIds, List<String> itemIds, boolean onSale);
}
