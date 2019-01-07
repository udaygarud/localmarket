package com.lmp.app.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.validation.Valid;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.lmp.app.model.BaseResponse;
import com.lmp.app.model.CartResponse;
import com.lmp.app.model.NewStoreUserRequest;
import com.lmp.app.model.StoreProfile;
import com.lmp.app.model.StoreRequest;
import com.lmp.app.model.UploadInventory;
import com.lmp.app.service.StoreService;
import com.lmp.app.utils.ValidationErrorBuilder;
import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.pojo.StoreEntity;
import com.lmp.db.pojo.StoreEntity.StoreCapabilities;
import com.lmp.db.pojo.StoreEntityTime;
import com.lmp.db.pojo.StoreItemEntity;
import com.lmp.db.pojo.UserEntity;
import com.lmp.db.repository.StoreRepository;
import com.lmp.db.repository.UserRepository;

@RestController
public class StoreController extends BaseController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private StoreService storeService;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  StoreRepository storeRepo;
  //lat=37.356752&lng=-121.999249&size=5
  @GetMapping("/store/nearby")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> lookupStoreNearBy(@RequestParam("lat") double lat
      , @RequestParam("lng") double lng, @RequestParam("radius") int radius) {
    logger.info("searching for stores nearby lat {} & lng {}", lat, lng);
    GeoResults<StoreEntity> stores = storeService.getStoresAround(lat, lng, radius);
    if (stores == null) {
      logger.debug("no stores found nearby lat {} & lng {}", lat, lng);
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<GeoResults<StoreEntity>>(stores, HttpStatus.OK);
  }

  @GetMapping("/store/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> getStore(@PathVariable("id") String id) {
    logger.info("getting store details for id {}", id);
    StoreEntity store = storeService.getStoreById(id);
    if(store == null) {
      logger.debug("no store found for id {}", id);
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<StoreEntity>(store, HttpStatus.OK);
  }
  
  @RequestMapping(value = "/store/register-new", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> registerStore(@Valid @RequestBody StoreRequest sRequest, Errors errors) {
    
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    System.out.println(sRequest.getLocation());
    StoreEntity store = storeService.registerStore(sRequest);
    //current time in milliseconds
    long currentDateTime = System.currentTimeMillis();
   
    //creating Date from millisecond
//    Date currentDate = new Date(currentDateTime);
//    System.out.println("current Date: " +currentDateTime+" date "+ currentDate);
//    DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
//    System.out.println("Milliseconds to Date: " + df.format(currentDate));

    StoreEntityTime st = new StoreEntityTime();
    st.setId(store.getId());
    st.setLocation(store.getLocation());
    st.setName(store.getName());
    st.setPhoneNumber(store.getPhoneNumber());
    st.setStoreOwner(store.getStoreOwner());
    st.setAddress(store.getAddress());
    st.setCapabilities(store.getCapabilities());
    st.setFranchise(store.getFranchise());
    st.setTime(currentDateTime);
    if(store == null) {
      logger.debug("store registration failed");
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<StoreEntityTime>(st, HttpStatus.OK);
  }

  @RequestMapping(value = "/user/register-new", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> registerUser(@Valid @RequestBody NewStoreUserRequest sRequest, Errors errors) {
    
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }

      UserEntity user = userRepo.findByEmail(sRequest.getEmail());
      if(user != null) {
        ObjectMapper mapper = new ObjectMapper();
        logger.debug("User registration failed");
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("message", "User Already Present");
 
        return new ResponseEntity<ObjectNode>(objectNode,HttpStatus.INTERNAL_SERVER_ERROR);
      }

      UserEntity entity = new UserEntity();
      entity.setId(sRequest.getEmail());
      entity.setEmail(sRequest.getEmail());
      entity.setFirstName(sRequest.getFirstName());
      entity.setLastName(sRequest.getLastName());
      entity.setPhoneNumber(sRequest.getPhoneNumber());
      entity.setUserName(sRequest.getUserName());
      userRepo.save(entity);

/* 
    if(user == null) {
      logger.debug("store registration failed");
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    } */
    return new ResponseEntity<UserEntity>(entity, HttpStatus.OK);
  }
 
  @RequestMapping(value = "/store/update-storeProfile", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> updateStoreInventory(@Valid @RequestBody StoreProfile storeRequest, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.debug("no stores found nearby lat {} & lng {}", storeRequest.toString());
    StoreEntity entity =new StoreEntity();
    if(!Strings.isNullOrEmpty(storeRequest.getStoreId())){
     entity = storeService.getStoreById(storeRequest.getStoreId());
    if(entity!=null){
      	entity.setId(storeRequest.getStoreId());
    	entity.setStoreOwner(new UserEntity().setId(storeRequest.getUserId()));
    	entity.setName(storeRequest.getName());
    	entity.setFranchise(storeRequest.getFranchise());
    	entity.setPhoneNumber(storeRequest.getPhoneNumber());
    	entity.setAddress(storeRequest.getAddress());
    	storeRepo.save(entity);
       }
    else {
    	ObjectMapper mapper = new ObjectMapper();
        logger.debug("User updation failed");
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("message", "User Not Present");
 
        return new ResponseEntity<ObjectNode>(objectNode,HttpStatus.INTERNAL_SERVER_ERROR);
        
        
      }
  
    }
    return new ResponseEntity<StoreEntity>(entity, HttpStatus.OK);    
  }

  
}
