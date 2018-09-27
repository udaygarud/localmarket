package com.lmp.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.lmp.app.bootup.AppBootUp;

@SpringBootApplication(scanBasePackages = { "com.lmp" })
@EnableCaching
@EnableWebMvc
public class Application implements CommandLineRunner {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private AppBootUp appBootUp;
  
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info("application stating");
    appBootUp.buildItemRepo();
  }

}
