package dev.jsedano.redis.movies.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class RedisConfig {

  @Bean
  public JedisPooled getJedisPooled() {
    return new JedisPooled("localhost", 6379);
  }
}
