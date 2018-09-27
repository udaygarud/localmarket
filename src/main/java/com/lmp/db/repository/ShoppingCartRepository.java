package com.lmp.db.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lmp.db.pojo.ShoppingCartEntity;

public interface ShoppingCartRepository extends MongoRepository<ShoppingCartEntity, String> {

}
