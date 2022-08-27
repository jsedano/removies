package dev.jsedano.redis.movies.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;

@Service
public class RedisDAO {

    @Autowired
    private JedisPooled jedisPooled;


    public void add(){
        jedisPooled.sadd("hola", "mundo");
    }
}
