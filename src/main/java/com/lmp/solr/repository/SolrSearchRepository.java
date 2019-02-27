package com.lmp.solr.repository;

import org.springframework.data.solr.repository.SolrCrudRepository;

import com.lmp.solr.entity.ItemDoc;

public interface SolrSearchRepository extends SolrCrudRepository<ItemDoc, String>, SolrCustomRepository {

//  @Query("brand:0* OR categories_suggestion_text:0*")
//  public List<ItemDoc> findByBrandORCategories(String query);
	public ItemDoc findByUpc(long upc);
}
