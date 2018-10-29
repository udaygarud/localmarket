package com.lmp.solr;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.lmp.app.entity.PriceRange;
import com.lmp.app.model.SearchRequest;
import com.lmp.solr.entity.ItemDoc;
import com.lmp.solr.entity.ItemField;
import com.lmp.solr.entity.KeywordDoc;
import com.lmp.solr.entity.QueryUtils;
import com.lmp.solr.repository.SolrKeyWordRepository;
import com.lmp.solr.repository.SolrSearchRepository;

@Service
public class SolrSearchService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static ItemField DEFAULT_FIELD = ItemField.CONTENT;
  @Autowired
  private SolrSearchRepository solrRepo;
  @Autowired
  private SolrKeyWordRepository keyWordRepo;

  private Criteria addSearchConditions(SearchRequest sRequest, List<String> storeIds) {
    // do or query for brand, as we want exact search
    Criteria conditions = QueryUtils.andQuery(DEFAULT_FIELD,
        Splitter.on(" ").splitToList(Strings.nullToEmpty(sRequest.getQuery())));
    if (sRequest.brandFilter() != null) {
      // do and brand name, as we want exact search
      // do and with query fields in content field
      conditions = conditions.connect().and(QueryUtils.oRIsQuery(ItemField.BRAND, sRequest.brandFilter()));
    }
    if (sRequest.categoryFilter() != null) {
      conditions = conditions.connect().and(QueryUtils.oRIsQuery(ItemField.CATEGORIES, sRequest.categoryFilter()));
    }
    if (sRequest.upcFilter() != null) {
      conditions = conditions.connect().and(QueryUtils.oRIsQuery(ItemField.UPC, sRequest.upcFilter()));
    }
    if(storeIds != null) {
      conditions = conditions.connect().and(QueryUtils.orQuery(ItemField.STORES, storeIds));
    }
    if(sRequest.priceFilter() != null) {
      PriceRange pr = PriceRange.from(sRequest.priceFilter());
      conditions = conditions.connect().and(QueryUtils.between(ItemField.MIN_PRICE, pr.getMin(), pr.getMax()));
    }
    return conditions;
  }

  public Page<ItemDoc> search(SearchRequest sRequest) {
    return search(sRequest, null);
  }

  public Page<ItemDoc> search(SearchRequest sRequest, List<String> storesIds) {
    // if no query or no brand/category/upc name filter present
    if (Strings.isNullOrEmpty(sRequest.getQuery()) && !sRequest.isSolrSearchNeeded()) {
      logger.error("invalid request. blank query and brand/category/upc filter. Returning empty page");
      return Page.empty();
    }
    Criteria conditions = addSearchConditions(sRequest, storesIds);
    SimpleQuery query = new SimpleQuery(conditions, sRequest.pageRequesst());
    if (sRequest.getFields() != null && sRequest.getFields().size() > 0) {
      // add mandetory field
      sRequest.getFields().add(ItemField.ID.getValue());
      query.addProjectionOnFields(sRequest.getFields().toArray(new String[] {}));
    }
    logger.info("searching for solr query {}", conditions.toString());
    return solrRepo.search(query);
  }

  public Page<ItemDoc> getAllDocs(int page, int size) {
    return solrRepo.findAll(new PageRequest(page, size));
  }

  public List<KeywordDoc> searchKeywords(String q) {
    Page<KeywordDoc> docs = keyWordRepo.findByKeyword(q);
    if (docs != null) {
      return docs.getContent();
    }
    return new ArrayList<KeywordDoc>();
  }

  @Cacheable("solr-facet-docs")
  public FacetPage<ItemDoc> facetSearch(SearchRequest sRequest, List<String> storesIds, List<ItemField> facetField) {
    if (facetField == null) {
      logger.error("facet field missing");
      return null;
    }
    // if no query or no brand/category/upc name filter present
    if (Strings.isNullOrEmpty(sRequest.getQuery()) && Strings.isNullOrEmpty(sRequest.getStoreId()) 
        && !sRequest.isSolrSearchNeeded()) {
      logger.error("invalid request. blank query and brand/category/upc filter. Returning empty page");
      return null;
    }
    Criteria conditions = addSearchConditions(sRequest, storesIds);
    logger.info("searching for solr facet query {}", conditions.toString());
    FacetOptions fo = new FacetOptions();
    for (ItemField itemField : facetField) {
      fo.addFacetOnField(itemField.getValue());
    }
    fo.setPageable(sRequest.pageRequesst());
    SimpleFacetQuery facetQuery = new SimpleFacetQuery(conditions)
        .setFacetOptions(fo);
    facetQuery.setRows(1);
    return solrRepo.facetSearch(facetQuery, null);
  }

  @Cacheable("min-max-price")
  public float sortAndMinOrMax(SearchRequest sRequest, List<String> storesIds, ItemField facetField, boolean min) {
    if (facetField == null) {
      logger.error("facet field missing");
      return 0;
    }
 // if no query or no brand/category/upc name filter present
    if (Strings.isNullOrEmpty(sRequest.getQuery()) && !sRequest.isSolrSearchNeeded()) {
      logger.error("invalid request. blank query and brand/category/upc filter. Returning empty page");
      return 0;
    }
    Criteria conditions = addSearchConditions(sRequest, storesIds);
    SimpleQuery query = new SimpleQuery(conditions,new PageRequest(0, 1));
    // add mandetory field
    sRequest.getFields().add(facetField.getValue());
    query.addProjectionOnFields(sRequest.getFields().toArray(new String[] {}));
    query.addSort(new Sort(
        new Sort.Order(min ? Sort.Direction.ASC : Sort.Direction.DESC, facetField.getValue())));
    logger.info("searching for solr query {}", conditions.toString());
    Page<ItemDoc> docPage = solrRepo.search(query);
    if(docPage != null && docPage.getContent() != null && !docPage.getContent().isEmpty()) {
      return min ? docPage.getContent().get(0).getMinPrice() : docPage.getContent().get(0).getMaxPrice();
    }
    return 0;
  }
}
