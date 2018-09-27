package com.lmp.solr.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.FacetPage;

import com.lmp.solr.entity.ItemDoc;

public interface SolrCustomRepository {

  Page<ItemDoc> search(SimpleQuery query);
  long count(SimpleQuery query);
  FacetPage<ItemDoc> facetSearch(SimpleFacetQuery facetQuery, FilterQuery filterQuery);
}
