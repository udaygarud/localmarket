package com.lmp.db.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.lmp.db.pojo.ShoppingWishListEntity;

public interface ShoppingWishListRepository extends MongoRepository<ShoppingWishListEntity, String> {

	
}


