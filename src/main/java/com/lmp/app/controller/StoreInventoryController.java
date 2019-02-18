package com.lmp.app.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.google.common.base.Strings;
import com.lmp.app.entity.Inventory;
import com.lmp.app.entity.Item;
import com.lmp.app.entity.ProductInformation;
import com.lmp.app.entity.StoreInformation;
import com.lmp.app.entity.StoreInventoryV2;
import com.lmp.app.model.BaseResponse;
import com.lmp.app.model.CartResponse;
import com.lmp.app.model.DeleteInventory;
import com.lmp.app.model.ResponseFilter;
import com.lmp.app.model.SearchProductID;
import com.lmp.app.model.SearchRequest;
import com.lmp.app.model.SearchResponse;
import com.lmp.app.model.UploadmultiInventory;
import com.lmp.app.model.UploadInventory;
import com.lmp.app.model.validator.SearchRequestValidator;
import com.lmp.app.service.AutoCompleteService;
import com.lmp.app.service.ItemService;
import com.lmp.app.service.ResultsFilterService;
import com.lmp.app.service.StoreInventoryService;
import com.lmp.app.utils.ValidationErrorBuilder;
import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.pojo.StoreItemEntity;
import com.lmp.db.repository.StoreInventoryRepository;
import com.lmp.solr.indexer.SolrIndexer;
import com.sun.tools.javac.jvm.Items;

import org.apache.solr.client.solrj.SolrServerException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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
  private AutoCompleteService autoCService;

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
      @RequestHeader(value = "emailId",required = false) String emailId, @RequestHeader(value = "uId",required = false) String uId) {
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

  @RequestMapping(value = "/getProductInfo", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> getProductInfo(@Valid @RequestBody SearchProductID UpcRequest, Errors errors) {
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }
    System.out.println(UpcRequest.toString() + " Hi IN PRODUCT ");
    List<ItemEntity> items = new ArrayList<ItemEntity>();
    List<String> upcs = UpcRequest.getListOfUpc();
    for (String upc : upcs) {
      ItemEntity item = itemservice.findByUpc(Long.parseLong(upc));
      items.add(item);
    }
    
    List<ProductInformation> prodList =new ArrayList<ProductInformation>();   
    ProductInformation productInfo = new ProductInformation();
    Map<String,List<String>> stores = new HashMap<>();
    for(int i=0 ;i<items.size();i++){
    	List<StoreInformation> storeInfo = service.getStoreswithInfo(items.get(i).getId());
    	List<String> list = new ArrayList<>();
    	
    	for(StoreInformation store : storeInfo){
    		if(store.getStock()>0){
    			items.get(i).setInStock(true);
    		}
    		list.add(store.getStoreId());
    	}
      stores.put(items.get(i).getId(), list);
      productInfo.setItem(items.get(i));	
      productInfo.setStores(list);
      prodList.add(productInfo);
   }
    return new ResponseEntity<List<ProductInformation>>(prodList, HttpStatus.OK);
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
      sItem.setList_price((float) uploadRequest.getListPrice()); // min 0.6 factor for price
      sItem.setSalePrice(sItem.getList_price());
      siRepo.save(sItem);
      try {
        indexer.addToIndex(response, (String) uploadRequest.getStoreId(), (float) uploadRequest.getListPrice(),
            (float) uploadRequest.getListPrice());
        autoCService.addsuggestKeywords(response);
      } catch (SolrServerException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    item = service.findByStoreIdanditemid(uploadRequest.getStoreId(), response.getId());
    service.updateStockCountafterInventoryUpdate(item, uploadRequest.getStock());
    // return new ResponseEntity<String>("Uploaded inventory", HttpStatus.OK);
    return new ResponseEntity<CartResponse>(
        BaseResponse.responseStatus(com.lmp.app.entity.ResponseStatus.MOVED_TO_LIST), HttpStatus.OK);
  }

  @RequestMapping(value = "/update-inventory", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> updateStoreInventory(@Valid @RequestBody UploadInventory uploadRequest, Errors errors) {
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
      sItem.setList_price((float) uploadRequest.getListPrice()); // min 0.6 factor for price
      sItem.setSalePrice(sItem.getList_price());
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
    item.setList_price((float) uploadRequest.getListPrice());
    service.resetStockCountafterInventoryUpdate(item, uploadRequest.getStock());
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
        sItem.setList_price((float) uploadRequest.getListPrice()); // min 0.6 factor for price
        sItem.setSalePrice(sItem.getList_price());
        siRepo.save(sItem);
      }
      item = service.findByStoreIdanditemid(uploadRequest.getStoreId(), response.getId());
      System.out.println("stokkkkkkkkk "+item.getStock());
      System.out.println("stokkkkkkkkk upload "+uploadRequest.getStock());
      service.updateStockCountafterInventoryUpdate(item, uploadRequest.getStock());
      
      autoCService.addsuggestKeywords(response);
    }

    // return new ResponseEntity<String>("Uploaded inventory", HttpStatus.OK);
    return new ResponseEntity<CartResponse>(
        BaseResponse.responseStatus(com.lmp.app.entity.ResponseStatus.MOVED_TO_LIST), HttpStatus.OK);
  }
  
  @RequestMapping(value = "/filters", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> getFilters(@Valid @RequestBody SearchRequest sRequest, Errors errors) {
       System.out.println(sRequest.toString() + " Hi");
    if (errors.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
    }

    List<ResponseFilter> facets = filterService.getFiltersFor(sRequest);

    System.out.println("response "+facets.toString());
    return new ResponseEntity<List<ResponseFilter>>(facets, HttpStatus.OK);
  }
  
  @RequestMapping(value = "/setOnSale",method =RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> setOnSaleProducts(@Valid @RequestBody UploadInventory updateRequest, Errors errors){
	  if (errors.hasErrors()) {
	      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
	    }
	  ItemEntity response = itemservice.findByUpc(updateRequest.getUpc());

	    StoreItemEntity item = service.findByStoreIdanditemid(updateRequest.getStoreId(), response.getId());
	    if (item == null) {
	      StoreItemEntity sItem = new StoreItemEntity();
	      long time = System.currentTimeMillis();
	      sItem.setStoreId(updateRequest.getStoreId());
	      sItem.getItem().setId(response.getId());
	      sItem.setStock(0);
	      sItem.setAdded(time);
	      sItem.setUpdated(time);
	      sItem.setList_price((float) updateRequest.getListPrice()); // min 0.6 factor for price
	      sItem.setSalePrice(sItem.getList_price());
	      siRepo.save(sItem);
	      try {
	        indexer.addToIndex(response, (String) updateRequest.getStoreId(), (float) updateRequest.getListPrice(),
	            (float) updateRequest.getListPrice());
	      } catch (SolrServerException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      }
	    }
	    item = service.findByStoreIdanditemid(updateRequest.getStoreId(), response.getId());

	    service.updateOnsale(item, updateRequest.getSalePrice());
	    // return new ResponseEntity<String>("Uploaded inventory", HttpStatus.OK);
	    return new ResponseEntity<CartResponse>(
	        BaseResponse.responseStatus(com.lmp.app.entity.ResponseStatus.PRODUCT_UPDATED), HttpStatus.OK);
  }
  
  @RequestMapping(value = "/removeProductsFromOnsale",method =RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> removeProductsFromOnsale(@Valid @RequestBody UploadInventory updateRequest, Errors errors){
	  if (errors.hasErrors()) {
	      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
	    }
	  ItemEntity response = itemservice.findByUpc(updateRequest.getUpc());

	    StoreItemEntity item = service.findByStoreIdanditemid(updateRequest.getStoreId(), response.getId());
	    if (item == null) {
	      StoreItemEntity sItem = new StoreItemEntity();
	      long time = System.currentTimeMillis();
	      sItem.setStoreId(updateRequest.getStoreId());
	      sItem.getItem().setId(response.getId());
	      sItem.setStock(0);
	      sItem.setAdded(time);
	      sItem.setUpdated(time);
	      sItem.setList_price((float) updateRequest.getListPrice()); // min 0.6 factor for price
	      sItem.setSalePrice(sItem.getList_price());
	      siRepo.save(sItem);
	      try {
	        indexer.addToIndex(response, (String) updateRequest.getStoreId(), (float) updateRequest.getListPrice(),
	            (float) updateRequest.getListPrice());
	      } catch (SolrServerException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      }
	    }
	    item = service.findByStoreIdanditemid(updateRequest.getStoreId(), response.getId());
	    service.removeFromOnsale(item, updateRequest.getSalePrice());
	    // return new ResponseEntity<String>("Uploaded inventory", HttpStatus.OK);
	    return new ResponseEntity<CartResponse>(
	        BaseResponse.responseStatus(com.lmp.app.entity.ResponseStatus.PRODUCT_UPDATED), HttpStatus.OK);
  }
  
  @RequestMapping(value = "/delete-inventory",method =RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<?> deleteInventoryFromStore(@Valid @RequestBody DeleteInventory inventories, Errors errors){
	  if (errors.hasErrors()) {
	      return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
	    }
	  
	  	for(int i=0;i<inventories.getIds().size();i++){
	  		System.out.println("ids "+inventories.getIds().get(i));
	  		service.deleveInventory(inventories.getIds().get(i));
	  	}
	    // return new ResponseEntity<String>("Uploaded inventory", HttpStatus.OK);
	    return new ResponseEntity<CartResponse>(
	        BaseResponse.responseStatus(com.lmp.app.entity.ResponseStatus.PRODUCT_DELETED), HttpStatus.OK);
  }
}
