package com.lmp.app.bootup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.lmp.app.service.AutoCompleteService;
import com.lmp.app.utils.FileIOUtil;
import com.lmp.config.ConfigProperties;
import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.pojo.StoreEntity;
import com.lmp.db.pojo.StoreItemEntity;
import com.lmp.db.pojo.UserEntity;
import com.lmp.db.repository.CustomerOrderRepository;
import com.lmp.db.repository.ItemRepository;
import com.lmp.db.repository.SearchHistoryRepository;
import com.lmp.db.repository.ShoppingCartRepository;
import com.lmp.db.repository.ShoppingWishListRepository;
import com.lmp.db.repository.StoreInventoryRepository;
import com.lmp.db.repository.StoreRepository;
import com.lmp.db.repository.UserRepository;
import com.lmp.solr.indexer.SolrIndexer;

@Component
public class AppBootUp {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  ConfigProperties prop;
  @Autowired
  private ShoppingCartRepository cartRepo;
  @Autowired
  private ShoppingWishListRepository wishListRepo;
  @Autowired
  private SearchHistoryRepository searchRepo;
  @Autowired
  private ItemRepository itemRepo;  
  @Autowired
  private StoreRepository storeRepo;
  @Autowired
  private StoreInventoryRepository siRepo;
  @Autowired
  private SolrIndexer indexer;
  @Autowired
  private AutoCompleteService autoCService;
  @Autowired
  private UserRepository userRepo;
  @Autowired
  private CustomerOrderRepository orderRepo;

  private void seedOneCategory(File file, List<StoreEntity> stores) throws IOException, SolrServerException {
    ObjectMapper objectMapper = new ObjectMapper();
    
    List<ItemEntity> items = objectMapper.readValue(file, new TypeReference<List<ItemEntity>>() {});
    logger.info("Seeding data file: {}", file.getName());
    Iterator<ItemEntity> it = items.listIterator();
    while(it.hasNext()) {
    
      ItemEntity item = it.next();
      System.out.println(item.getCategories().contains("beer") + " ..hi");
      if( !item.getCategories().contains("beer") && !item.getCategories().contains("wine") && !item.getCategories().contains("vodka") && !item.getCategories().contains("tequila")
      && !item.getCategories().contains("hard ciders") && !item.getCategories().contains("spirits") && 
      !item.getCategories().contains("types of whiskey") &&  !item.getCategories().contains("malt drinks")){
        System.out.println(item + " hi");
        continue; 
      }
      try {
        itemRepo.save(item);
        Object[] values = fillStoreInventory(item, stores);
        indexer.addToIndex(item, (String)values[0], (Float)values[1], (Float)values[2]);
      } catch (DuplicateKeyException e) {
        logger.info("found duplicate item upc {}", item.getUpc());
        logger.error(e.getMessage(), e);
        it.remove();
      }
    }
    // index documents
    //indexer.addToIndex(items);
    logger.info("Added & indexed {} items from file {}", items.size(), file.getName());
    FileIOUtil.writeProgress(prop.getSeededFiles(), file.getName());
  }

  private Object[] fillStoreInventory(ItemEntity item, List<StoreEntity> stores) {
    List<String> storeIdsToIndex = new ArrayList<>();
    Random random = new Random();
    Float min = Float.MAX_VALUE;
    Float max = 0f;

    for(StoreEntity store : stores) {
      // System.out.println(store.getName());
      // if(store.getName().equals("Wineyards") || store.getName().equals("Capital Stores") || store.getName().equals("Nita Wines") )
      // {
      //   System.out.println("reached here");
      //   continue;
      // }
      if(item.canGoOnStoreInventory(store)) {
        StoreItemEntity sItem = new StoreItemEntity();
        long time = System.currentTimeMillis();
        sItem.setStoreId(store.getId());
        sItem.getItem().setId(item.getId());
        sItem.setStock(100);
        sItem.setAdded(time);
        sItem.setUpdated(time);
        sItem.setList_price(item.getList_price() * (0.6f + random.nextFloat())); // min 0.6 factor for price
        storeIdsToIndex.add(store.getId());
        if(random.nextInt(100) < 20) {
          // put 20% inventory on sale 
          sItem.setOnSale(true);
          sItem.setSalePrice(sItem.getList_price() * (1 - ((10.0f +random.nextInt(30)))/100)); // min 10 percent discount
        } else {
          sItem.setSalePrice(sItem.getList_price());
        }
        min = Math.min(min, sItem.getSalePrice());
        max = Math.max(max, sItem.getSalePrice());
        siRepo.save(sItem);
      }
    }
    return new Object[]{Joiner.on(" ").join(storeIdsToIndex), min, max};
  }

  private void seedTestUsers() {
    List<String> emailIds = new ArrayList<>();
    emailIds.add("123");
    /* emailIds.add("skawaleonline@gmail.com");
    emailIds.add("sumit@plmlogix.com");
    emailIds.add("store1-owner@plmlogix.com");
    emailIds.add("store2-owner@plmlogix.com");
    emailIds.add("store3-owner@plmlogix.com"); */
    for (String string : emailIds) {
      UserEntity entity = new UserEntity();
      entity.setId(string);
      entity.setEmail(string);
      entity.setFirstName("testuser");
      entity.setFirstName(string.split("@")[0]);
      userRepo.save(entity);
    }
  }
  private void seedStores() throws IOException{
    logger.info("Seeding stores : " + prop.getStoreSeedFile());
    ObjectMapper objectMapper = new ObjectMapper();
    List<StoreEntity> stores = objectMapper.readValue(
        new File(prop.getStoreSeedFile())
        , new TypeReference<List<StoreEntity>>(){});
    storeRepo.saveAll(stores);
  }

  public void buildItemRepo() throws IOException, SolrServerException {
    if(!prop.isDataSeedEnabled() || prop.getDataSeedDir() == null 
        || prop.getDataSeedDir().isEmpty()) {
      return ;
    }
    List<File> files = FileIOUtil.getAllFilesInDir(prop.getDataSeedDir());
    if(files == null || files.isEmpty()) {
      return ;
    }
    if(prop.isCleanupAndSeedData()) {
      cartRepo.deleteAll();
      itemRepo.deleteAll();
      indexer.deleteAll();
      siRepo.deleteAll();
      orderRepo.deleteAll();
      storeRepo.deleteAll();
      userRepo.deleteAll();
      wishListRepo.deleteAll();
      searchRepo.deleteAll();
      seedTestUsers();
      //seedStores();
      FileIOUtil.deleteFile(prop.getSeededFiles());
    }
    Set<String> processed = FileIOUtil.readProcessed(prop.getSeededFiles());
    List<StoreEntity> stores = storeRepo.findAll();
    for(File file : files) {
      if(processed.contains(file.getName())) {
        logger.info("skipping file: {}", file.getName());
        continue;
      }
      seedOneCategory(file, stores);
    }
   // autoCService.buildAutoCompleteCollection();
    indexer.deleteAllKeyWords();
  }
}
