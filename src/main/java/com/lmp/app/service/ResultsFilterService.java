package com.lmp.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.lmp.app.entity.PriceRange;
import com.lmp.app.model.ResponseFilter;
import com.lmp.app.model.SearchRequest;
import com.lmp.db.pojo.StoreEntity;
import com.lmp.solr.SolrSearchService;
import com.lmp.solr.entity.ItemField;

@Service
public class ResultsFilterService {

  @Autowired
  private SolrSearchService solrService;
  @Autowired
  private StoreService storeService;
  @Autowired
  private CategoryService categoryService;

  private ResponseFilter buildPriceRangeFilter(SearchRequest sRequest, List<String> storeIds) {
    // get the max price of the product in the search
    int max = (int)Math.ceil(
        solrService.sortAndMinOrMax(sRequest, storeIds, ItemField.MAX_PRICE, false));
    if(max == 0) {
      return null;
    }
    return ResponseFilter.fromList("price", 
        PriceRange.getDisplayNames(
            PriceRange.buildPriceRangeList(max)));
  }

  @Cacheable("response-filters")
  public List<ResponseFilter> getFiltersFor(SearchRequest sRequest) {
    List<ResponseFilter> facets = new ArrayList<>();
    Iterable<StoreEntity> stores = null;
    // Search for query across all the stores
    if (Strings.isNullOrEmpty(sRequest.getStoreId())) {
      stores = storeService.getStoresAround(sRequest);
    } else {
      // get all items in the store
      List<String> storeIdsToSearch = new ArrayList<>();
      List<String> ids = Splitter.on(",").splitToList(sRequest.getStoreId().trim());
      for (String id : ids) {
        storeIdsToSearch.add(id.trim());
      }
      stores = storeService.getStoreByIds(storeIdsToSearch);
      // search for query in store
    }
    List<String> storeIds = new ArrayList<>();
    stores.forEach(store -> {
      storeIds.add(store.getId());
    });
    List<ItemField> facetFields = new ArrayList<>();
    facetFields.add(ItemField.BRAND);
    //facetFields.add(ItemField.PRICE);
    // brand facet
    facets = ResponseFilter.buildResultFilter(facetFields, solrService.facetSearch(sRequest, storeIds, facetFields));
    // store facet
    facets.add(ResponseFilter.buildStoreFilter("store", stores));
    // category facet
    facets.add(ResponseFilter.fromList("category", categoryService.getCategories(sRequest.categoryFilter(), stores)));
    // price facet
    ResponseFilter priceFilter = buildPriceRangeFilter(sRequest, storeIds);
    if(priceFilter != null) {
      facets.add(priceFilter);
    }
    // onsale filter
    facets.add(ResponseFilter.booleanFilter("onsale"));
    return facets;
  }
}
