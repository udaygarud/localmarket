package com.lmp.db.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lmp.db.pojo.ItemEntity;

public interface ItemRepository extends MongoRepository<ItemEntity, String> {

    public ItemEntity findByUpc(long upc);
    public List<ItemEntity> findAllByUpc(long upc);
    public List<ItemEntity> findAllByCategories(String category);
}