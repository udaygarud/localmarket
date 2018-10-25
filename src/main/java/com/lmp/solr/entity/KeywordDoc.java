package com.lmp.solr.entity;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "keyworddoc")
public class KeywordDoc {

  @Id
  private String id;

  @Field("keyword")
  private String keyword;
  @Field("original")
  private String original;
  @Field("priority")
  private int priority = 5;

  public KeywordDoc() {
  }
  public KeywordDoc(String keyword) {
    this.original = keyword.trim();
    this.keyword = keyword.toLowerCase().trim();
  }

  public KeywordDoc(String keyword, int p) {
    this.original = keyword.trim();
    this.keyword = keyword.toLowerCase().trim();
    this.priority = p;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getOriginal() {
    return original;
  }
  public void setOriginal(String original) {
    this.original = original;
  }
  public int getPriority() {
    return priority;
  }
  public void setPriority(int priority) {
    this.priority = priority;
  }
  @Override
  public boolean equals(Object o) {
    KeywordDoc kd = (KeywordDoc) o;
    return kd.keyword.toLowerCase().equals(this.keyword.toLowerCase());
  }

  @Override
  public int hashCode(){
    return this.keyword.hashCode();
  }
}
