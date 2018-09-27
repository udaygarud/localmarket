package com.lmp.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import com.lmp.config.ConfigProperties;

@Configuration
@EnableSolrRepositories(basePackages={"com.lmp.solr"})
public class SolrContext {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ConfigProperties prop;

  @Bean
  public SolrClient solrClient() {
      return new HttpSolrClient.Builder(prop.getSolrHost()).build();
  }

  @Bean
  public SolrTemplate solrTemplate(SolrClient client) throws Exception {
      return new SolrTemplate(client);
  }
}