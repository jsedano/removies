package dev.jsedano.redis.movies.controller;

import dev.jsedano.redis.movies.dao.RedisDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {

  @Autowired private RedisDAO redisDAO;

  @RequestMapping(method = RequestMethod.GET, value = "/v1/javainuse")
  public String sayHello() {
    redisDAO.add();
    return "hello world";
  }
}
