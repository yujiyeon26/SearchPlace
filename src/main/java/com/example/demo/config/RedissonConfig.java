package com.example.demo.config;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
public class RedissonConfig {
    
	private RedisProperties redisProperties;
	
    @Bean(name = "redissonClient")
    public RedissonClient redissonClientSingle(@Value("${spring.data.redis.host}") String host,@Value("${spring.data.redis.port}") String port) throws IOException {
  
        RedissonClient redisson = null;
        Config config = new Config();
        final Codec codec = new StringCodec();
        config.setCodec(codec);
        config.useSingleServer().
        setAddress("redis://"+host+":"+port).
        setConnectionPoolSize(100);
        redisson = Redisson.create(config); 
        return redisson;
    }
   
    /* Cluster 구성 했을때.
    @Profile({"prod","dev"})
    @Bean(name = "redissonClient")
    public RedissonClient redissonClientCluster() throws IOException {
        String[] nodes = redisProperties.getUrl().split(",");   
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = "redis://" + nodes[i];
        }
        RedissonClient redisson = null;
        Config config = new Config();
        final Codec codec = new StringCodec(); // redis-cli에서 보기 위해
        config.setCodec(codec);
        config.useClusterServers()
                .setScanInterval(2000)     
                .addNodeAddress(nodes);
        redisson = Redisson.create(config);   
        return redisson;
    }
    */
}
