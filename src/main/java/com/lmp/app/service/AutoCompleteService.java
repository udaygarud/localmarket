package com.lmp.app.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.lmp.db.pojo.ItemEntity;
import com.lmp.db.pojo.StoreItemEntity;
import com.lmp.solr.SolrSearchService;
import com.lmp.solr.entity.KeywordDoc;
import com.lmp.solr.indexer.SolrIndexer;

@Component
public class AutoCompleteService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ItemService itemService;
  @Autowired
  private SolrIndexer indexer;
  @Autowired
  private SolrSearchService solrService;

  public Set<String> suggest(String q) {
    List<String> result = new ArrayList<>();
    Set<String> results = new HashSet<>();
    if(Strings.isNullOrEmpty(q)) {
      return results;
    }
    List<KeywordDoc> keywords = solrService.searchKeywords(q);
    for (KeywordDoc keywordDoc : keywords) {
    	results.add(keywordDoc.getOriginal());
    }
    return results;
  }
  public void buildAutoCompleteCollection() {
    int page = 0;
    int size = 5000;
    Set<KeywordDoc> tokens = new HashSet<>();
    while(true) {
      logger.info("requesting for documents page {} and size{}", page, size);
      Iterable<ItemEntity> items = itemService.getAllDocs(page, size);
      if(items == null || !items.iterator().hasNext()) {
        logger.info("finished pulling all item docs");
        break;
      }
      logger.info("received documents from page: {}", page);
      Iterator<ItemEntity> it = items.iterator();
      while(it.hasNext()) {
        ItemEntity item = it.next();
        for(String cat : item.getCategories()) {
          // highest priority in auto complete
          tokens.add(new KeywordDoc(cat, 0));
        }
        // second highest priority in auto complete
        tokens.add(new KeywordDoc(item.getBrand(), 1));
        // lowest priority in auto complete, priority 5
        tokens.add(new KeywordDoc(item.getTitle()));
      }
      //go for next page
      page++;
    }
    logger.info("starting to index the keywords. total keywords {}", tokens.size());
    indexer.deleteAllKeyWords();
    for (KeywordDoc token : tokens) {
      indexer.addKeyWord(token);
    }
    logger.info("finished indexing {} keywords", tokens.size());
  }
  public void addsuggestKeywords(ItemEntity sItem){
	  Set<KeywordDoc> tokens = new HashSet<>();
	  Set<String> allTokens = new HashSet<>();
	  Iterator<KeywordDoc> docs = solrService.getAllKeywords();
	  while(docs.hasNext()){
		  allTokens.add(docs.next().getOriginal());
	  }
	  System.out.println("category "+Iterables.getLast(sItem.getCategories()));
	  if(allTokens.add(Iterables.getLast(sItem.getCategories()))){
		  System.out.println("added");
		  tokens.add(new KeywordDoc(Iterables.getLast(sItem.getCategories()), 0));
	  }
	  
//	  for(String cat : sItem.getCategories()) {
//          // highest priority in auto complete
//		  if(allTokens.add(cat)){
//			  tokens.add(new KeywordDoc(cat, 0));
//		  }
//          
//        }
        // second highest priority in auto complete
	  if(allTokens.add(sItem.getBrand())){
        tokens.add(new KeywordDoc(sItem.getBrand(), 1));
	  }
        // lowest priority in auto complete, priority 5
	  if(allTokens.add(sItem.getTitle())){
		 tokens.add(new KeywordDoc(sItem.getTitle()));
	  }
       
        for (KeywordDoc token : tokens) {
            indexer.addKeyWord(token);
          }
  }
}
