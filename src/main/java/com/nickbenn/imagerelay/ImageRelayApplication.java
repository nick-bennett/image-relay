package com.nickbenn.imagerelay;

import okhttp3.OkHttpClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class ImageRelayApplication {

  public static void main(String[] args) {
    SpringApplication.run(ImageRelayApplication.class, args);
  }

  @Bean
  public OkHttpClient getClient() {
    return new OkHttpClient();
  }

}

