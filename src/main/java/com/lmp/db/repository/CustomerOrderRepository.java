package com.lmp.db.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.lmp.db.pojo.CustomerOrderEntity;

public interface CustomerOrderRepository extends MongoRepository<CustomerOrderEntity, String> {

  Page<CustomerOrderEntity> findAllByCustomerId(String id, Pageable page);

  Page<CustomerOrderEntity> findAllByCustomerIdAndStatus(String id, String status, Pageable page);

  Page<CustomerOrderEntity> findAllByStoreId(String id, Pageable page);
  
  Optional<CustomerOrderEntity> findByIdAndStoreId(String id, String storeId);

  
  Page<CustomerOrderEntity> findAllByStoreIdAndStatus(String id, String status, Pageable page);

}
