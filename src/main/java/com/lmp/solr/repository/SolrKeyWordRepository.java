package com.lmp.solr.repository;

import org.springframework.data.solr.repository.SolrCrudRepository;

import com.lmp.solr.entity.KeywordDoc;

public interface SolrKeyWordRepository extends SolrCrudRepository<KeywordDoc, String>, SolrKeyWordCustomRepository {

}
