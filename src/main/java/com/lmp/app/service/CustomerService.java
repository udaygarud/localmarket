package com.lmp.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lmp.app.entity.Customer;
import com.lmp.app.exceptions.UserIdAlreadyExistException;
import com.lmp.db.pojo.UserEntity;
import com.lmp.db.repository.UserRepository;

@Service
public class CustomerService {

  @Autowired
  private UserRepository repo;

  public UserEntity registerUser(Customer request) {
    Optional<UserEntity> optional = repo.findById(request.getId());
    if(optional.isPresent()) {
      throw new UserIdAlreadyExistException();
    }
    UserEntity  entity = UserEntity.fromPojo(request);
    entity = repo.save(entity);
    return entity;
  }
}
