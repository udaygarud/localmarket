package com.lmp.db.pojo;

import java.util.LinkedHashMap;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="searchHistory")
public class SearchHistoryEntity {

	@Id
	private String id;
	private   LinkedHashMap<Integer,String> query;
 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LinkedHashMap<Integer, String> getQuery() {
		return query;
	}
	public void setQuery(LinkedHashMap<Integer, String> query) {
		this.query = query;
	} 
}
