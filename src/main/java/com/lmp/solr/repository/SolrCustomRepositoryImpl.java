package com.lmp.solr.repository;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.FacetPage;

import com.lmp.solr.entity.ItemDoc;

public class SolrCustomRepositoryImpl implements SolrCustomRepository {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Resource
  private SolrTemplate solrTemplate;

  @Override
  public Page<ItemDoc> search(SimpleQuery query) {
    Page<ItemDoc> itemDocs = solrTemplate.query("itemdoc", query, ItemDoc.class);
    if(itemDocs == null || itemDocs.getContent() == null) {
      logger.error("null response from solr for query: {}", query.toString());
      System.out.println("if");
      return null;
    }
    logger.debug("solr docs found: {}, for query {}",itemDocs.getContent().size(), query.toString());
    System.out.println("----------------- "+itemDocs.getContent().size());
    return itemDocs;
  }

  @Override
  @Cacheable("results-count")
  public long count(SimpleQuery countQuery) {
    if (countQuery == null) {
      return 0;
    }
    long count = solrTemplate.count("itemdoc", countQuery);
    logger.debug("solr docs count: {}, for query {}", count, countQuery.toString());
    return count;
  }

  public FacetPage<ItemDoc> facetSearch(SimpleFacetQuery facetQuery, FilterQuery filterQuery) {
    if(filterQuery != null) {
      facetQuery.addFilterQuery(filterQuery);
    }
    FacetPage<ItemDoc> page = solrTemplate.queryForFacetPage("itemdoc", facetQuery, ItemDoc.class);
    if(page == null || page.getContent() == null) {
      logger.error("null response from solr for query: {}", facetQuery.toString());
      return null;
    }
    logger.debug("solr docs found: {}, for query {}",page.getContent().size(), facetQuery.toString());
    return page;    
  }
}
