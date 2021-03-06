  package com.lmp.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.lmp.app.entity.Inventory;
import com.lmp.app.entity.Item;
import com.lmp.app.entity.ShoppingWishList;
import com.lmp.app.entity.ShoppingCart.CartItem;
import com.lmp.app.entity.ShoppingWishList.WishItem;
import com.lmp.app.entity.StoreInformation;
import com.lmp.app.exceptions.ItemNotInStockException;
import com.lmp.app.model.BaseResponse;
import com.lmp.app.model.SearchRequest;
import com.lmp.app.model.SearchResponse;
import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.pojo.SearchHistoryEntity;
import com.lmp.db.pojo.StoreItemEntity;
import com.lmp.db.repository.CustomStoreInventoryRepository;
import com.lmp.db.repository.SearchHistoryRepository;
import com.lmp.db.repository.StoreInventoryRepository;
import com.lmp.solr.SolrSearchService;
import com.lmp.solr.entity.ItemDoc;

import ch.qos.logback.core.net.SyslogOutputStream;

@Service
public class StoreInventoryService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  StoreInventoryRepository repo;
  @Autowired
  SolrSearchService solrService;
  @Autowired
  StoreService storeService;
  @Autowired
  ItemService itemService;
  @Autowired
  private StoreInventoryRepository siRepo;
  @Autowired
  SearchHistoryRepository historyRepo;
  @Autowired
  private ShoppingWishService wishservice;
  
  public Item findStoreItemByUpc(long upc, String storeId) {
	  ItemEntity entity = itemService.findByUpc(upc);
	  if(entity == null) {
		return null;
	  }
	  StoreItemEntity storeItem = repo.findByStoreIdAndItemId(storeId, entity.getId());
	  return storeItem == null ? null : Item.fromStoreInventoryEntity(storeItem);
  }

   public Page<ItemDoc> searchSolr(SearchRequest sRequest, List<String> storeIds) {
    // check if we need solr search for the request
    Page<ItemDoc> results = solrService.search(sRequest, storeIds);
    if (results == null || results.getTotalElements() <= 0 || results.getTotalElements() <= sRequest.fetchedCount()) {
      logger.info("no solr results results");
      return Page.empty();
    }
    return results;
  }

  private BaseResponse searchDBForDocs(SearchRequest sRequest, List<String> storeIds, Page<ItemDoc> docs, boolean v2) {
    Page<StoreItemEntity> items = null;
    List<WishItem> wishList = new ArrayList<>();
    if(wishservice.getCart(sRequest.getEmail()) != null){
    	wishList = wishservice.getCart(sRequest.getEmail()).getItems();
    }
     
	   //System.out.println("list "+list.getItems().size());
    if (docs != null) {
      List<String> ids = new ArrayList<>();
      docs.getContent().forEach(itemDoc -> {
        ids.add(itemDoc.getId());
       
      });
      // search items in solr first then retrieve those from MongoDB
      logger.info("going for mongo with {}", ids.toString());
      if (sRequest.isOnSaleRequest()) {
        items = repo.findAllByStoreIdInAndItemIdInAndOnSaleTrue(storeIds, ids,
            new PageRequest(0, sRequest.getRows()));
      } else {
        items = repo.findAllByStoreIdInAndItemIdIn(storeIds, ids, new PageRequest(0, sRequest.getRows()));
      }
      Map<String,List<StoreInformation>> storemap = new HashMap<>();
      System.out.println("items found "+items.getNumberOfElements());
      for(StoreItemEntity ie : items) {
       // List<String> Dbstores = getStores(ie.getItem().getId());
    	  System.out.println("items "+ie.getItem().getCategories());
    	  List<StoreInformation> Dbstores = getStoreswithInfo(ie.getItem().getId());
        List<StoreInformation> filteredstores = new ArrayList<>();
        for (StoreInformation storeInformation : Dbstores) {
        	if(storeIds.contains(storeInformation.getStoreId())){
                filteredstores.add(storeInformation);
              }
		}
        Dbstores.forEach(i -> {
          
        });
        storemap.put(ie.getId(), filteredstores); 

       }
      //storeService.getStores("5bade68f11632a18ad6e768f");
      return SearchResponse.buildStoreInventoryResponse(items, docs.getTotalElements(), sRequest.getPage(), v2 ,storemap,wishList);
    } else {
      
      // search for all within store
      if (sRequest.isOnSaleRequest()) {
        items = repo.findAllByStoreIdInAndOnSaleTrue(storeIds, sRequest.pageRequesst());
      } else {
        items = repo.findAllByStoreIdIn(storeIds, sRequest.pageRequesst());
      }
    }
    Map<String,List<StoreInformation>> storemap = new HashMap<>();
      for(StoreItemEntity ie : items) {
        List<StoreInformation> Dbstores = getStoreswithInfo(ie.getItem().getId());
        List<StoreInformation> filteredstores = new ArrayList<>();
        for (StoreInformation storeInformation : Dbstores) {
        	 if(storeIds.contains(storeInformation.getStoreId())){
                 filteredstores.add(storeInformation);
               }
		}
        storemap.put(ie.getId(), filteredstores);
       }
    return SearchResponse.buildStoreInventoryResponse(items, v2,storemap,wishList);
  }

  @Cacheable("store-items")
  public BaseResponse search(SearchRequest sRequest, boolean v2) {
    List<String> storeIdsToSearch = new ArrayList<>();
    System.out.println(sRequest.getStoreId() + "..stores");
    if (Strings.isNullOrEmpty(sRequest.getStoreId())) {
      // get all store around the user location
      storeIdsToSearch = storeService.getStoresIdsAround(sRequest);
      
    } else {
      // just search in store id selected
      List<String> ids = Splitter.on(",").splitToList(sRequest.getStoreId().trim());
      for (String id : ids) {
        storeIdsToSearch.add(id.trim());
      }
    }
    Page<ItemDoc> docs = null;    
    if (sRequest.isSolrSearchNeeded()) {
      // search solr and then mongo for this request
      docs = searchSolr(sRequest, storeIdsToSearch);
      System.out.println("no of elements "+docs.getNumberOfElements());
      if(docs == null || docs.getTotalElements() == 0) {
        return SearchResponse.empty();
      }
    }
    // dont search solr, directly search in mongo
    return searchDBForDocs(sRequest, storeIdsToSearch, docs, v2);
  }

  
	public void insertHistoryBasedOnUID(String uId,String query) {
		SearchHistoryEntity entity = historyRepo.findByUIdAndEmailId(uId, "");
		LinkedHashMap<Integer, String> hm = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
		List<String> list = new ArrayList<>();
		Set<String> searchedQuery= new HashSet<>();;
		if (entity!=null) {
			updateSearchHistory(entity,"",uId,query);
		} else {
			SearchHistoryEntity userentity = new SearchHistoryEntity();
			userentity.setuId(uId);
			userentity.setEmailId("");
			hm.put(0, query);
			userentity.setQuery(hm);
			searchedQuery.add(query);
			historyRepo.save(userentity);
		}
	}
	public void insertHistoryBasedOnEmailAndUID(String emailId,String uId,String query) {
		SearchHistoryEntity entityForEmailAndUid = historyRepo.findByEmailIdAndUId(emailId, uId);
	//	SearchHistoryEntity entryForEmail = historyRepo.findByEmailId(emailId);
		//SearchHistoryEntity entryForUid = historyRepo.findByUId(uId);	 
		LinkedHashMap<Integer, String> hm = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
		List<String> list = new ArrayList<>();
		Set<String> searchedQuery= new HashSet<>();;
		if (entityForEmailAndUid!=null) {
			//System.out.println(" user present ");
			updateSearchHistory(entityForEmailAndUid,emailId,uId,query);
		}
//		else if(entryForEmail!= null){
//			updateSearchHistory(entryForEmail,emailId,uId,query,"emailId");
//		}
//		else if(entryForUid!=null){
//			updateSearchHistory(entryForUid,emailId,uId,query,"uId");
//		}
			else {
		
			SearchHistoryEntity userentity = new SearchHistoryEntity();
			//entity = users;
			userentity.setEmailId(emailId);
			userentity.setuId(uId);	
			hm.put(0, query);
			userentity.setQuery(hm);
			searchedQuery.add(query);
			historyRepo.save(userentity);
		}
	}
	public void updateSearchHistory(SearchHistoryEntity entity,String emailId,String uId,String query){
		LinkedHashMap<Integer, String> hm = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, String> map = new LinkedHashMap<Integer, String>();
		List<String> list = new ArrayList<>();
		hm = entity.getQuery();
		Boolean flag = false;
		int key = 0;
		for (Map.Entry<Integer, String> entry : hm.entrySet()) {
			key = entry.getKey();
			if(entry.getValue()!=null && query!=null){
				list.add(entry.getValue());
				if (entry.getValue().equalsIgnoreCase(query)) {
					flag = true;
				}	
			}
		}
		if (!flag) {
			if(query!=null){
			list.add(query);
			hm.put(key++, query);
			}
		}
		for(int i=0;i<list.size();i++) {
			map.put(i, list.get(i));     
		    }
		
		entity.setId(entity.getId());
		entity.setuId(uId);
		entity.setEmailId(emailId);
		entity.setQuery(map);
		historyRepo.save(entity);
		}
	

	public Map<Integer,String> getHistory(String uId,String emailId){
		SearchHistoryEntity searchItem = historyRepo.findByEmailIdAndUId(emailId,uId);
		Map<Integer,String> map = new HashMap<Integer, String>();
		if(searchItem!=null){
			return searchItem.getQuery();
		}
		else{
			return map;
		}
	}

  
  public Optional<StoreItemEntity> findStoreItemById(String id) {
    Optional<StoreItemEntity> sItem = repo.findById(id);
     return sItem.isPresent() ? sItem : null;
    //return sItem.isPresent() ? sItem.get().toCartItem() : null;
  }
  
  public CartItem findById(String id) {
	    Optional<StoreItemEntity> sItem = repo.findById(id);
	     
	    return sItem.isPresent() ? sItem.get().toCartItem() : null;
	  }
  
  public Optional<StoreItemEntity> findByid(String id) {
	    Optional<StoreItemEntity> sItem = repo.findById(id);
	    return sItem;
	   // return sItem.isPresent() ? sItem.get().toWishItem() : null;
	  }

  
  public StoreItemEntity findByStoreIdanditemid(String storeid, String itemid) {
    StoreItemEntity sItem = repo.findByStoreIdAndItemId(storeid,itemid);
    return sItem;
  }

  public Map<String, Integer> getInStockCount(List<String> ids) {
    Iterable<StoreItemEntity> items = repo.findAllById(ids);
    Map<String, Integer> map = new HashMap<>();
    if(items == null) {
      return null;
    }
    items.iterator().forEachRemaining(item -> {
      map.put(item.getId(), item.getStock());
    });
    return map;
  }

   public List<String> getStores(String itemid){
    List<StoreItemEntity> result = repo.findByItemId(itemid);
    
    List<String> onlyStoreid = new ArrayList<>();
    if(result!=null){
      result.forEach(item ->{
    	 
        onlyStoreid.add(item.getStoreId());
      });
      return onlyStoreid;
    }else{
      return null;
    }
    
  } 
   
   public List<StoreInformation> getStoreswithInfo(String itemid){
	    List<StoreItemEntity> result = repo.findByItemId(itemid);
	    System.out.println("---- "+itemid);
	    List<StoreInformation> storeInfo = new ArrayList<>();
	    if(result!=null){
	      result.forEach(item ->{
	    	  StoreInformation store = new StoreInformation();
	    	  store.setStoreId(item.getStoreId());
	    	  store.setOnSale(item.isOnSale());
	    	  store.setOfferPrice(item.getSalePrice());
	    	  store.setStock(item.getStock());
	    	  store.setItemId(item.getId());
	    	  if(item.getStock()>0){
	    		  store.setInStock(true);
	    	  }else {
	    		  store.setInStock(false);
	    	  }
	    	  storeInfo.add(store);
	      });
	      return storeInfo;
	    }else{
	      return null;
	    }
	    
	  } 
  
  
  public void verifyItemStock(List<CartItem> items) {
    if(items == null) {
      return;
    }
    List<String> ids = new ArrayList<>();
    items.forEach(item -> {
      ids.add(item.getId());
    });
    Map<String, Integer> map = getInStockCount(ids);
    ItemNotInStockException outOfStockException = new ItemNotInStockException();
    for (CartItem item : items) {
      if (item.getQuantity() > map.getOrDefault(item.getId(), 0)) {
        outOfStockException.getOutOfStockItems().add(item.getId());
      }
    }
    if (outOfStockException.getOutOfStockItems().size() > 0) {
      throw outOfStockException;
    }
  }

  @Transactional
  public boolean updateStockCount(List<CartItem> cartItems, boolean increment) {
    if(cartItems == null || cartItems.size() == 0) {
      return false;
    }
    Map<String, Integer> map = new HashMap<>();
    cartItems.forEach(item -> {
      map.put(item.getId(), item.getQuantity());
    });
    Iterable<StoreItemEntity> items = repo.findAllById(map.keySet());
    if(items == null) {
      return false;
    }
    items.forEach(item -> {
      item.setStock(increment ? item.getStock() + map.get(item.getId()) : item.getStock() - map.get(item.getId()));
    });
    repo.saveAll(items);
    return true;
  }

  @Transactional
  public boolean updateStockCountafterInventoryUpdate(StoreItemEntity item, int stock) {
    item.setStock( item.getStock() + stock);
    System.out.println("---------- "+item.getStock());
    repo.save(item);
    return true;
  }

  @Transactional
  public boolean resetStockCountafterInventoryUpdate(StoreItemEntity item, int stock) {
    item.setStock( stock);
    repo.save(item);
    return true;
  }
  
  @Transactional
  public boolean updateOnsale(StoreItemEntity item,float salePrice){
	  item.setOnSale(true);
	  item.setSalePrice(salePrice);
	  repo.save(item);
	  return true;
  }
  @Transactional
  public boolean removeFromOnsale(StoreItemEntity item,float salePrice){
	  item.setOnSale(false);
	  item.setSalePrice(item.getList_price());
	  repo.save(item);
	  return true;
  }
  
  @Transactional
  public boolean deleveInventory(String id){
	  repo.deleteById(id);
	  return true;
  }
 

}
