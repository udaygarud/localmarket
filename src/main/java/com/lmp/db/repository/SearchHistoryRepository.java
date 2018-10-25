package com.lmp.db.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lmp.db.pojo.SearchHistoryEntity;
import com.lmp.db.pojo.UserEntity;

public interface SearchHistoryRepository extends MongoRepository<SearchHistoryEntity, String> {

}
