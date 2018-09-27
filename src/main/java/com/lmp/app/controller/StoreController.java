package com.lmp.app.controller;

import javax.validation.Valid;

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

import com.lmp.app.model.StoreRequest;
import com.lmp.app.service.StoreService;
import com.lmp.app.utils.ValidationErrorBuilder;
import com.lmp.db.pojo.StoreEntity;

@RestController
public class StoreController extends BaseController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private StoreService storeService;

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
    StoreEntity store = storeService.registerStore(sRequest);
    if(store == null) {
      logger.debug("store registration failed");
      return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<StoreEntity>(store, HttpStatus.OK);
  }
}
