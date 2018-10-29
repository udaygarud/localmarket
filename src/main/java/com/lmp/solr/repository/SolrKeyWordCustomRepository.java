package com.lmp.solr.repository;

import org.springframework.data.domain.Page;

import com.lmp.solr.entity.KeywordDoc;

public interface SolrKeyWordCustomRepository {

  public Page<KeywordDoc> findByKeyword(String query);
}
