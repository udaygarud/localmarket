package com.lmp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@EnableMongoRepositories(basePackages = {"com.lmp.db.repository"})
public class MongoConfiguration extends AbstractMongoConfiguration {

  @Autowired
  private ConfigProperties prop;

  @Bean
  public Mongo mongo() throws Exception {
    return new MongoClient(prop.getMongoDbHost(), prop.getMongoDbPort());
  }

  @Override
  public String getDatabaseName() {
    return prop.getMongoDbName();
  }

  @Override
  public String getMappingBasePackage() {
    return "com.lmp.db.pojo";
  }

  @Override
  public MongoClient mongoClient() {
    return new MongoClient(prop.getMongoDbHost(), prop.getMongoDbPort());
  }
}