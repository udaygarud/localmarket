package com.lmp.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.google.common.base.Strings;
import com.lmp.app.model.BaseResponse;
import com.lmp.app.model.CartResponse;
import com.lmp.app.model.ResponseFilter;
import com.lmp.app.model.SearchRequest;
import com.lmp.app.model.SearchResponse;
import com.lmp.app.model.UploadmultiInventory;
import com.lmp.app.model.UploadInventory;
import com.lmp.app.model.validator.SearchRequestValidator;
import com.lmp.app.service.ItemService;
import com.lmp.app.service.ResultsFilterService;
import com.lmp.app.service.StoreInventoryService;
import com.lmp.app.utils.ValidationErrorBuilder;
import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.pojo.StoreItemEntity;
import com.lmp.db.repository.StoreInventoryRepository;
import com.lmp.solr.indexer.SolrIndexer;

import org.apache.solr.client.solrj.SolrServerException;
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

@RestController
@RequestMapping("/store-inventory")
public class StoreInventoryController extends BaseController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  @Autowired
  private StoreInventoryRepository siRepo;
  @Autowired
  private StoreInventoryService service;
  @Autowired
  private ItemService itemservice;
  @Autowired
  private ResultsFilterService filterService;
  @Autowired
  private SolrIndexer indexer;
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
  public ResponseEntity<?> lookupStoreInventoryV2(@Valid @RequestBody SearchRequest searchRequest, Errors errors,
      @RequestHeader(value = "emailId") String emailId, @RequestHeader(value = "uId") String uId) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.info("searching for the request " + searchRequest.toString());
    // emailId , uId- unique id
    // when uId is only present
    if (!Strings.isNullOrEmpty(searchRequest.getQuery())) {
      // if(!uId.equals("") && uId!=null && emailId.isEmpty()||emailId.equals(null)){
      if (!uId.equals("") && uId != null && Strings.isNullOrEmpty(emailId)) {
        service.insertHistoryBasedOnUID(uId, searchRequest.getQuery());
      }
      // when both present
      else if (uId != null && !uId.isEmpty() && !emailId.isEmpty() && emailId != null) {
        service.insertHistoryBasedOnEmailAndUID(emailId, uId, searchRequest.getQuery());
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
  public ResponseEntity<?> getHistory(@RequestParam(value = "emailId", required = false) String emailId,
      @RequestParam(value = "uId", required = false) String uId) {
    SearchResponse<String> response = new SearchResponse<>();
    List<String> list = new ArrayList<>();
    System.out.println("In search History" + emailId + "Hui " + uId);
    // when uId is only present
    if (uId != null && !uId.equals("") && emailId.isEmpty()) {
      Map<Integer, String> map = service.getHistory(uId, "");
      logger.info("getting history for {}", emailId);
      if (!map.isEmpty()) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
          list.add(entry.getValue());
        }
        response.setResults(list);
      }
    }
    // when both present
    else if (uId != null && !uId.isEmpty() && !emailId.isEmpty() && emailId != null) {
      Map<Integer, String> map = service.getHistory(uId, emailId);
      logger.info("getting history for {}", emailId);
      if (!map.isEmpty()) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
          list.add(entry.getValue());
        }
        response.setResults(list);
      }
    }

    return new ResponseEntity<SearchResponse>(response, HttpStatus.OK);

  }

  @RequestMapping(value = "/upload-inventory", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> uploadStoreInventory(@Valid @RequestBody UploadInventory uploadRequest, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.debug("no stores found nearby lat {} & lng {}", uploadRequest.toString());
    ItemEntity response = itemservice.findByUpc(uploadRequest.getUpc());

    StoreItemEntity item = service.findByStoreIdanditemid(uploadRequest.getStoreId(), response.getId());
    if (item == null) {
      StoreItemEntity sItem = new StoreItemEntity();
      long time = System.currentTimeMillis();
      sItem.setStoreId(uploadRequest.getStoreId());
      sItem.getItem().setId(response.getId());
      sItem.setStock(0);
      sItem.setAdded(time);
      sItem.setUpdated(time);
      sItem.setListPrice((float) uploadRequest.getListPrice()); // min 0.6 factor for price
      sItem.setSalePrice(sItem.getListPrice());
      siRepo.save(sItem);
      try {
        indexer.addToIndex(response, (String) uploadRequest.getStoreId(), (float) uploadRequest.getListPrice(),
            (float) uploadRequest.getListPrice());
      } catch (SolrServerException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
    item = service.findByStoreIdanditemid(uploadRequest.getStoreId(), response.getId());
   
    System.out.println(item.getStock());
    service.updateStockCountafterInventoryUpdate(item, uploadRequest.getStock());
    System.out.println(item.getStock());
    // return new ResponseEntity<String>("Uploaded inventory", HttpStatus.OK);
    return new ResponseEntity<CartResponse>(
        BaseResponse.responseStatus(com.lmp.app.entity.ResponseStatus.MOVED_TO_LIST), HttpStatus.OK);
  }

  @RequestMapping(value = "/upload-multiinventory", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> uploadmultiStoreInventory(@Valid @RequestBody UploadmultiInventory uploadRequestMulti,
      Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    logger.debug("no stores found nearby lat {} & lng {}", uploadRequestMulti.getInventoryList().toString());
    List<UploadInventory> uploads = uploadRequestMulti.getInventoryList();
    for (UploadInventory uploadRequest : uploads) {
      ItemEntity response = itemservice.findByUpc(uploadRequest.getUpc());

      StoreItemEntity item = service.findByStoreIdanditemid(uploadRequest.getStoreId(), response.getId());
      if (item == null) {
        StoreItemEntity sItem = new StoreItemEntity();
        long time = System.currentTimeMillis();
        sItem.setStoreId(uploadRequest.getStoreId());
        sItem.getItem().setId(response.getId());
        sItem.setStock(0);
        sItem.setAdded(time);
        sItem.setUpdated(time);
        sItem.setListPrice((float) uploadRequest.getListPrice()); // min 0.6 factor for price
        sItem.setSalePrice(sItem.getListPrice());
        siRepo.save(sItem);
      }
      item = service.findByStoreIdanditemid(uploadRequest.getStoreId(), response.getId());
      System.out.println(item.getStock());
      service.updateStockCountafterInventoryUpdate(item, uploadRequest.getStock());
      System.out.println(item.getStock());
    }

    // return new ResponseEntity<String>("Uploaded inventory", HttpStatus.OK);
    return new ResponseEntity<CartResponse>(
        BaseResponse.responseStatus(com.lmp.app.entity.ResponseStatus.MOVED_TO_LIST), HttpStatus.OK);
  }

}
