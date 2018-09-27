package com.lmp.db.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lmp.db.pojo.UserEntity;

public interface UserRepository extends MongoRepository<UserEntity, String> {

}
