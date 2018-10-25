package com.lmp.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lmp.app.entity.CategoryNode;
import com.lmp.app.model.BaseResponse;
import com.lmp.app.model.ResponseFilter;
import com.lmp.app.model.SearchRequest;
import com.lmp.app.model.SearchResponse;
import com.lmp.app.model.validator.SearchRequestValidator;
import com.lmp.app.service.ResultsFilterService;
import com.lmp.app.service.StoreInventoryService;
import com.lmp.app.utils.ValidationErrorBuilder;

@RestController
@RequestMapping("/store-inventory")
public class StoreInventoryController extends BaseController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private StoreInventoryService service;
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
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
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
  public ResponseEntity<?> lookupStoreInventoryV2(@Valid @RequestBody SearchRequest searchRequest, Errors errors,@RequestHeader(value="emailId") String emailId,@RequestHeader(value="uId") String uId ) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("searching for the request " + searchRequest.toString());
    //emailId , uId- unique id 
    if(!emailId.equals(null)&& !emailId.equals("")){
    	System.out.println("in email "+emailId);
    	service.insertHistory(emailId,searchRequest.getQuery());	
    }else{
    	System.out.println(" email blanck");
    	if(!uId.equals("")){
    		service.insertHistory(uId,searchRequest.getQuery());	
    	}
    }
    BaseResponse response = service.search(searchRequest, true);
    // logger.info("getting store details for store id {}", storeId);

    if (response == null) {
      logger.info("no results for request {}", searchRequest.toString());
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<BaseResponse>(response, HttpStatus.OK);
  }
  
  @RequestMapping(value = "/searchHistory", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> getHistory(@RequestParam(value = "emailId", required = false) String emailId) { 
	SearchResponse<String> response = new SearchResponse<>();
	List<String> list = new ArrayList<>();
    if(!emailId.equals(null)&& !emailId.equals("")){ 	
    	Map<Integer,String> map=service.getHistory(emailId);
    	logger.info("getting history for {}", emailId);
    	if(!map.isEmpty()){
    	for (Map.Entry<Integer, String> entry : map.entrySet()) {
			list.add(entry.getValue());
		}
        response.setResults(list);
    	}
    }
    
    return new ResponseEntity<SearchResponse>(response, HttpStatus.OK);

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
}
