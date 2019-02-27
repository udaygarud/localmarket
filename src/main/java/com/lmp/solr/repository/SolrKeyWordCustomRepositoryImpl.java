package com.lmp.solr.repository;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;

import com.google.common.base.Strings;
import com.lmp.solr.entity.KeywordDoc;
import com.lmp.solr.entity.QueryUtils;

public class SolrKeyWordCustomRepositoryImpl implements SolrKeyWordCustomRepository {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Resource
  private SolrTemplate solrTemplate;

  @Override
  public Page<KeywordDoc> findByKeyword(String q) {
    if(Strings.isNullOrEmpty(q)) {
      return Page.empty();
    }
    SimpleQuery query = new SimpleQuery();
    //query.addCriteria(QueryUtils.andQuery("keyword", q.trim().toLowerCase()));
    query.addCriteria(QueryUtils.andQuerySuggest("keyword", q.trim().toLowerCase()));
    query.addSort(Sort.by("priority"));
    query.setOffset(0L);
    query.setRows(10);
    Page<KeywordDoc> docs = solrTemplate.query("keyworddoc", query, KeywordDoc.class);
    if(docs == null || docs.getContent() == null) {
      logger.error("null response from solr for query: {}", query.toString());
      return null;
    }
    return docs;
  }

}
