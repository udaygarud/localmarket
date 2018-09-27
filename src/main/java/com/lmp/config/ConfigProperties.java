package com.lmp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lmp.app")
public class ConfigProperties {

  private boolean dataSeedEnabled;
  private boolean cleanupAndSeedData;
  private String solrHost;
  private String dataSeedDir;
  private String storeSeedFile;
  private String seededFiles;
  private String mongoDbHost;
  private int mongoDbPort;
  private String mongoDbName;

  public boolean isDataSeedEnabled() {
    return dataSeedEnabled;
  }

  public void setDataSeedEnabled(boolean dataSeed) {
    this.dataSeedEnabled = dataSeed;
  }

  public boolean isCleanupAndSeedData() {
    return cleanupAndSeedData;
  }

  public void setCleanupAndSeedData(boolean cleanupAndSeedData) {
    this.cleanupAndSeedData = cleanupAndSeedData;
  }

  public String getSolrHost() {
    return solrHost;
  }

  public void setSolrHost(String solrHost) {
    this.solrHost = solrHost;
  }

  public String getDataSeedDir() {
    return dataSeedDir;
  }

  public void setDataSeedDir(String dataSeedDir) {
    this.dataSeedDir = dataSeedDir;
  }

  public String getStoreSeedFile() {
    return storeSeedFile;
  }

  public void setStoreSeedFile(String storeSeedFile) {
    this.storeSeedFile = storeSeedFile;
  }

  public String getSeededFiles() {
    return seededFiles;
  }

  public void setSeededFiles(String seededFiles) {
    this.seededFiles = seededFiles;
  }

  public String getMongoDbHost() {
    return mongoDbHost;
  }

  public void setMongoDbHost(String mongoDbHost) {
    this.mongoDbHost = mongoDbHost;
  }

  public int getMongoDbPort() {
    return mongoDbPort;
  }

  public void setMongoDbPort(int mongoDbPort) {
    this.mongoDbPort = mongoDbPort;
  }

  public String getMongoDbName() {
    return mongoDbName;
  }

  public void setMongoDbName(String mongoDbName) {
    this.mongoDbName = mongoDbName;
  }
}