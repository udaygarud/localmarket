package com.lmp.db.repository;


import java.util.List;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.lmp.db.pojo.StoreEntity;

public interface StoreRepository extends MongoRepository<StoreEntity, String> {

  public StoreEntity findByName(String name);

  public StoreEntity findByStoreOwnerId(String email);

  public StoreEntity findByStoreOwnerEmail(String email);

  public List<StoreEntity> findAllByFranchise(String franchise);

  public GeoResults<StoreEntity> findByLocationWithin(Circle circle);

  public GeoResults<StoreEntity> findByLocationNear(Point point, Distance distance);
}