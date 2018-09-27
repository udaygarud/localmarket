package com.lmp.db.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.lmp.db.pojo.StoreItemEntity;

public interface StoreInventoryRepository
    extends MongoRepository<StoreItemEntity, String>{

  public StoreItemEntity findByStoreIdAndItemId(String storeId, String itemId);
  
  public StoreItemEntity findByStoreId(String storeId);
 
  public Page<StoreItemEntity> findAllByStoreIdIn(List<String> id, Pageable page);

  public Page<StoreItemEntity> findAllByStoreIdInAndOnSaleTrue(List<String> id, Pageable page);

  public Page<StoreItemEntity> findAllByStoreIdInAndItemIdIn(List<String> storeIds, List<String> ids, Pageable page);

  public Page<StoreItemEntity> findAllByStoreIdInAndItemIdInAndOnSaleTrue(List<String> storeIds, List<String> ids,
      Pageable page);

}
