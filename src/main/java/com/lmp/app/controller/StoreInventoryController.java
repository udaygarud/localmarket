package com.lmp.app.controller;

import java.util.List;

import javax.validation.Valid;

import com.lmp.app.model.BaseResponse;
import com.lmp.app.model.ResponseFilter;
import com.lmp.app.model.SearchRequest;
import com.lmp.app.model.UploadInventory;
import com.lmp.app.model.validator.SearchRequestValidator;
import com.lmp.app.service.ItemService;
import com.lmp.app.service.ResultsFilterService;
import com.lmp.app.service.StoreInventoryService;
import com.lmp.app.utils.ValidationErrorBuilder;
import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.pojo.StoreItemEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store-inventory")
public class StoreInventoryController extends BaseController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private StoreInventoryService service;
  @Autowired
  private ItemService itemservice;
  @Autowired
  private ResultsFilterService filterService;

  private SearchRequestValidator searchRequestValidator;

  @Autowired
  public StoreInventoryController(SearchRequestValidator searchRequestValidator) {
      this.searchRequestValidator = searchRequestValidator;
  }

  @InitBinder("searchRequest")
  public void setupBinder(WebDataBinder binder) {
      binder.addValidators(searchRequestValidator);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> lookupStoreInventory(@Valid @RequestBody SearchRequest searchRequest, Errors errors) {
    System.out.println("Hi");
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
   
    System.out.println(searchRequest.getQuery() + "Request");
    logger.info("searching for the request " + searchRequest.toString());
    BaseResponse response = service.search(searchRequest, false);
    // logger.info("getting store details for store id {}", storeId);

    if (response == null) {
      logger.info("no results for request {}", searchRequest.toString());
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<BaseResponse>(response, HttpStatus.OK);
  }

  
  @RequestMapping(value = "v2/search", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> lookupStoreInventoryV2(@Valid @RequestBody SearchRequest searchRequest, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("searching for the request " + searchRequest.toString());
    BaseResponse response = service.search(searchRequest, true);
    // logger.info("getting store details for store id {}", storeId);

    if (response == null) {
      logger.info("no results for request {}", searchRequest.toString());
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<BaseResponse>(response, HttpStatus.OK);
  }
  
  @RequestMapping(value = "/filters", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> lookupStoreInventoryFilters(@Valid @RequestBody SearchRequest searchRequest, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("searching for the request {}", searchRequest);
    List<ResponseFilter> response = filterService.getFiltersFor(searchRequest);
    // logger.info("getting store details for store id {}", storeId);

    if (response == null) {
      logger.info("no results for request {}", searchRequest.toString());
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<List<ResponseFilter>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = "/upload-inventory" , method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> uploadStoreInventory(@Valid @RequestBody UploadInventory uploadRequest, Errors errors){
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.debug("no stores found nearby lat {} & lng {}", uploadRequest.toString());
    ItemEntity response = itemservice.findByUpc(uploadRequest.getUpc());
    StoreItemEntity item = service.findByStoreIdanditemid(uploadRequest.getStoreId(),response.getId());
    System.out.println(item.getStock());
    service.updateStockCountafterInventoryUpdate(item, uploadRequest.getStock());
    System.out.println(item.getStock());
    return new ResponseEntity<String>("Uploaded inventory", HttpStatus.OK);
  }
}
