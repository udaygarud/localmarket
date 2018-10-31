package com.lmp.db.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.lmp.db.pojo.SearchHistoryEntity;
import com.lmp.db.pojo.StoreItemEntity;
import com.lmp.db.pojo.UserEntity;

public interface SearchHistoryRepository extends MongoRepository<SearchHistoryEntity, String> {
	//public SearchHistoryEntity findByEmailId(String emailId);
	public SearchHistoryEntity findByUId (String uId);
	public SearchHistoryEntity findByUIdAndEmailId(String uId,String emailId);
	public SearchHistoryEntity findByEmailIdAndUId(String email,String uid);

}
