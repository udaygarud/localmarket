package com.lmp.db.repository;

import java.util.List;

import org.springframework.data.solr.core.query.SimpleQuery;

import com.lmp.db.pojo.StoreItemEntity;

public interface CustomStoreInventoryRepository {

  public List<StoreItemEntity> search(SimpleQuery query);
}
