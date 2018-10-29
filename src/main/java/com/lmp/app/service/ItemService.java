package com.lmp.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.lmp.app.model.SearchRequest;
import com.lmp.app.model.SearchResponse;
import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.repository.ItemRepository;
import com.lmp.solr.SolrSearchService;
import com.lmp.solr.entity.ItemDoc;

@Service
public class ItemService {

  @Autowired
  private ItemRepository itemRepo;
  @Autowired
  private SolrSearchService solrService;

  public List<ItemEntity> find() {
    
    return null;
  }

  public ItemEntity findByUpc(long upc) {
    if(upc <= 0) {
      return null;
    }
    return itemRepo.findByUpc(upc);
  }

  public List<ItemEntity> findAllByUpc(long upc) {
    if(upc <= 0) {
      return null;
    }
    return itemRepo.findAllByUpc(upc);
  }

  public SearchResponse<ItemEntity> searchByText(SearchRequest sRequest) {
    if(sRequest == null) {
      return null;
    }
    Page<ItemDoc> results = solrService.search(sRequest);
    List<String> ids = new ArrayList<>();
    results.getContent().forEach(itemDoc -> {
      ids.add(itemDoc.getId());
    });
    Iterable<ItemEntity> items = itemRepo.findAllById(ids);
    return SearchResponse.buildItemResponse(results, items);
  }

  public Iterable<ItemEntity> getAllDocs(int page, int size) {
    Page<ItemDoc> results = solrService.getAllDocs(page, size);
    List<String> ids = new ArrayList<>();
    results.forEach(doc -> {
      ids.add(doc.getId());
    });
    Iterable<ItemEntity> items = itemRepo.findAllById(ids);
    return items;
  }
}
